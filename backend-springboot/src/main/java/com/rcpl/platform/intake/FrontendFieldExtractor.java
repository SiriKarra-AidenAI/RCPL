package com.rcpl.platform.intake;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rcpl.platform.intake.IntakeDtos.ExtractResultDto;
import com.rcpl.platform.intake.IntakeDtos.IntakeFieldDto;
import org.springframework.stereotype.Component;

/**
 * Byte-for-byte Java port of the frontend's deterministic (NO AI/LLM) regex/heuristic email
 * extractor: RCPL-Angular-Frontend/src/app/lib/extract.ts's {@code extractEmail()}. Same field
 * labels, same order, same regex tables (translated to {@code java.util.regex}), same
 * town/state/DB-keyword lookups, same priority/summary construction — this class exists purely
 * to reproduce that file's output contract on the server for {@code /api/extract} and
 * {@code /api/extract-document}.
 *
 * <p><b>Deliberately separate from {@link IntakeParser}.</b> IntakeParser has its own, different
 * field-label taxonomy ("Distributor Name", "Monthly Turnover", etc.) wired to the DB-persisting
 * {@code /api/intake/parse} and {@code /api/intake/ingest} endpoints (see {@link IntakeController}).
 * This class must not be merged with or substituted into that flow — it backs a separate,
 * parallel pair of endpoints ({@link ExtractController}) whose only contract is matching
 * extract.ts's shape.
 */
@Component
public class FrontendFieldExtractor {

    // A town/state/DB-keyword table entry, precompiled once at class-init.
    private record Town(String name, String stateAbbrev, Pattern pattern) {}

    private record StateRule(Pattern pattern, String name, String abbrev) {}

    private record DbKeyword(Pattern pattern, String label) {}

    // ---- extract.ts's `RX` ----
    private static final Pattern GST_RE = Pattern.compile("\\b\\d{2}[A-Z]{5}\\d{4}[A-Z][A-Z\\d]Z[A-Z\\d]\\b");
    private static final Pattern PHONE_RE = Pattern.compile("(?:\\+?91[\\s-]?)?[6-9]\\d{4}[\\s-]?\\d{5}\\b");
    private static final Pattern EMAIL_RE =
            Pattern.compile("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}", Pattern.CASE_INSENSITIVE);
    private static final Pattern TURNOVER_RE =
            Pattern.compile("₹?\\s*(\\d{2,4})\\s*L\\b", Pattern.CASE_INSENSITIVE);

    // ---- extract.ts's `DB_KEYWORDS` — order matters (first match wins) ----
    private static final List<DbKeyword> DB_KEYWORDS = List.of(
            new DbKeyword(Pattern.compile("replacement", Pattern.CASE_INSENSITIVE), "Replacement DB"),
            new DbKeyword(Pattern.compile("additional", Pattern.CASE_INSENSITIVE), "Additional DB"),
            new DbKeyword(Pattern.compile("gm\\s*excl", Pattern.CASE_INSENSITIVE), "GM Excl DB"),
            new DbKeyword(Pattern.compile("\\bgt\\s*db\\b|general trade", Pattern.CASE_INSENSITIVE),
                    "GT DB (with CSO/DSM)"),
            new DbKeyword(Pattern.compile("\\btraders?\\b", Pattern.CASE_INSENSITIVE), "Traders"));

    // ---- extract.ts's `STATES` — (regex, full name, abbreviation) ----
    private static final List<StateRule> STATES = List.of(
            new StateRule(Pattern.compile("maharashtra|\\bMH\\b", Pattern.CASE_INSENSITIVE), "Maharashtra", "MH"),
            new StateRule(Pattern.compile("gujarat|\\bGJ\\b", Pattern.CASE_INSENSITIVE), "Gujarat", "GJ"),
            new StateRule(Pattern.compile("\\bgoa\\b|\\bGA\\b", Pattern.CASE_INSENSITIVE), "Goa", "GA"));

    // ---- extract.ts's `TOWNS` — order matters ("Nashik Rural" checked before "Nashik") ----
    private static final List<Town> TOWNS = List.of(
            town("Nashik Rural", "MH"), town("Nashik", "MH"), town("Pune", "MH"), town("Mumbai", "MH"),
            town("Nagpur", "MH"), town("Aurangabad", "MH"), town("Kolhapur", "MH"), town("Chalisgaon", "MH"),
            town("Andheri", "MH"), town("Bhiwandi", "MH"), town("Surat", "GJ"), town("Vadodara", "GJ"),
            town("Ahmedabad", "GJ"), town("Panaji", "GA"));

    private static Town town(String name, String stateAbbrev) {
        return new Town(name, stateAbbrev, Pattern.compile("\\b" + name + "\\b", Pattern.CASE_INSENSITIVE));
    }

    private static final Pattern VENDOR_RE = Pattern.compile(
            "\\bvendor\\b|packaging|supplier|supply|iso ?9001|factory audit|manufactur|corrugat",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern WORD_START_RE = Pattern.compile("\\b\\w");
    private static final Pattern WHITESPACE_RE = Pattern.compile("\\s+");

    // firmName()'s 3 phrasings, tried in this order — case-SENSITIVE, exactly as in extract.ts
    // (none of these carry the `i` flag in the TS source).
    private static final Pattern FIRM_WE_ARE_RE = Pattern.compile("\\bwe are ([A-Z][\\w&.'\\- ]{2,44}?)[,.]");
    private static final Pattern FIRM_VERB_RE = Pattern.compile(
            "\\b([A-Z][\\w&.'\\- ]{2,44}?) (?:would like|services?|covers|is an|is a|handling|distributes|has )");
    private static final Pattern FIRM_FROM_RE = Pattern.compile("\\bfrom ([A-Z][\\w&.'\\- ]{2,44}?)[,.]");

    private static final Pattern SUBJECT_PREFIX_RE = Pattern.compile("^(re|fwd):\\s*", Pattern.CASE_INSENSITIVE);
    private static final Pattern SUBJECT_SPLIT_RE = Pattern.compile("[—\\-–|:]");
    private static final Pattern HAS_LETTER_RE = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
    private static final Pattern LOCAL_PART_SEP_RE = Pattern.compile("[._-]+");

    private static final Pattern HONORIFIC_RE =
            Pattern.compile("\\b(?:Mr\\.?|Ms\\.?|Mrs\\.?|Dr\\.?)\\s*[A-Z][.\\s]*[A-Za-z]+");
    // `im` flags in the TS source -> CASE_INSENSITIVE | MULTILINE.
    private static final Pattern SIGNOFF_RE = Pattern.compile(
            "(?:regards|thanks|sincerely|—|--)\\s*,?\\s*((?:[A-Z]\\.\\s*)?[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)?)"
                    + "\\s*[.!]?\\s*$",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /** extract.ts's `SHORT` — short names used in the "still missing ..." summary clause. */
    private static final Map<String, String> SHORT = new LinkedHashMap<>();
    static {
        SHORT.put("Firm / Agency Name", "firm name");
        SHORT.put("Contact Person", "contact");
        SHORT.put("Phone Number", "phone");
        SHORT.put("Email Address", "email");
        SHORT.put("Town / City", "town");
        SHORT.put("State", "state");
        SHORT.put("DB Type Requested", "DB type");
        SHORT.put("Turnover Claim (₹/mo)", "turnover");
        SHORT.put("GST Number", "GST");
    }

    /**
     * Port of {@code extractEmail({source, title, body})} — {@code subject} here is extract.ts's
     * {@code title} (the wire/request field is named "subject"; extract.ts's own local variable
     * for it is also literally called {@code subject}, bound from the {@code title} input key).
     */
    public ExtractResultDto extract(String source, String subject, String body) {
        String src = nullToEmpty(source);
        String subj = nullToEmpty(subject);
        String rawBody = nullToEmpty(body);
        String fullText = subj + "\n" + rawBody;

        String partnerType = VENDOR_RE.matcher(fullText).find() ? "vendor" : "distributor";

        Town town = findTown(fullText);
        StateRule stateHit = findState(fullText, town);

        String gst = firstMatch(GST_RE, fullText);
        String phone = firstMatch(PHONE_RE, rawBody);
        if (phone == null) phone = firstMatch(PHONE_RE, fullText);
        String email = firstMatch(EMAIL_RE, src);
        if (email == null) email = firstMatch(EMAIL_RE, fullText);
        String turnoverN = firstGroup(TURNOVER_RE, fullText);
        String dbType = findDbType(fullText);
        String firm = firmName(rawBody, src, subj);
        String contact = contactPerson(rawBody);
        boolean vendor = "vendor".equals(partnerType);

        List<IntakeFieldDto> fields = new ArrayList<>();
        fields.add(field("Firm / Agency Name", firm));
        fields.add(field("Contact Person", contact));
        fields.add(field("Phone Number", phone == null ? null : WHITESPACE_RE.matcher(phone).replaceAll(" ").trim()));
        fields.add(field("Email Address", email));
        fields.add(field("Town / City", town == null ? null : town.name()));
        fields.add(field("State", stateHit == null ? null : stateHit.name()));
        fields.add(field("DB Type Requested", vendor ? "Not applicable (Vendor)" : dbType));
        fields.add(field("Turnover Claim (₹/mo)", turnoverN == null ? null : "₹" + turnoverN + "L"));
        fields.add(field("GST Number", gst));

        int captured = 0;
        for (IntakeFieldDto f : fields) {
            if (f.ok()) captured++;
        }
        BigDecimal confidencePct = BigDecimal.valueOf(captured)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(fields.size()), 0, RoundingMode.HALF_UP);

        List<String> missing = new ArrayList<>();
        for (IntakeFieldDto f : fields) {
            if (!f.ok()) missing.add(SHORT.getOrDefault(f.label(), f.label().toLowerCase()));
        }

        int tnum = turnoverN != null ? Integer.parseInt(turnoverN) : 0;
        String priority = ("Replacement DB".equals(dbType) || tnum >= 200)
                ? "high"
                : (captured < 4 ? "low" : "normal");
        String region = town != null
                ? town.name() + ", " + town.stateAbbrev()
                : (stateHit != null ? stateHit.name() : null);

        // Summary built entirely from the extracted data — mirrors extract.ts line for line.
        StringBuilder summary = new StringBuilder(firm != null ? firm : "A prospective " + partnerType);
        if (town != null) summary.append(" in ").append(town.name());
        if (vendor) {
            summary.append(" (vendor enquiry)");
        } else if (dbType != null) {
            summary.append(" — ").append(dbType);
        }
        if (turnoverN != null) summary.append(", ~₹").append(turnoverN).append("L/mo");
        summary.append(". ").append(captured).append('/').append(fields.size()).append(" fields extracted");
        summary.append(missing.isEmpty() ? " — complete." : "; still missing " + String.join(", ", missing) + ".");

        return new ExtractResultDto(fields, summary.toString(), partnerType, priority, region, confidencePct,
                captured);
    }

    private Town findTown(String text) {
        for (Town t : TOWNS) {
            if (t.pattern().matcher(text).find()) return t;
        }
        return null;
    }

    private StateRule findState(String text, Town town) {
        for (StateRule s : STATES) {
            if (s.pattern().matcher(text).find()) return s;
        }
        if (town != null) {
            for (StateRule s : STATES) {
                if (s.abbrev().equals(town.stateAbbrev())) return s;
            }
        }
        return null;
    }

    private String findDbType(String text) {
        for (DbKeyword k : DB_KEYWORDS) {
            if (k.pattern().matcher(text).find()) return k.label();
        }
        return null;
    }

    /** Firm/agency name — try a few phrasings, then fall back to the email's local-part or the subject. */
    private String firmName(String body, String source, String subject) {
        for (Pattern p : List.of(FIRM_WE_ARE_RE, FIRM_VERB_RE, FIRM_FROM_RE)) {
            Matcher m = p.matcher(body);
            if (m.find()) return WHITESPACE_RE.matcher(m.group(1).trim()).replaceAll(" ");
        }
        String emailHit = firstMatch(EMAIL_RE, source);
        String em = (emailHit != null ? emailHit : source).split("@")[0];
        if (!em.isEmpty() && HAS_LETTER_RE.matcher(em).find()) {
            return titleCase(LOCAL_PART_SEP_RE.matcher(em).replaceAll(" ").trim());
        }
        String subj = SUBJECT_PREFIX_RE.matcher(subject).replaceFirst("");
        subj = SUBJECT_SPLIT_RE.split(subj, 2)[0].trim();
        return subj.isEmpty() ? null : subj;
    }

    /** Honorific match ("Mr./Ms./Dr. Name") first, else a name on a "regards/thanks/sincerely" signoff line. */
    private String contactPerson(String body) {
        Matcher honorific = HONORIFIC_RE.matcher(body);
        if (honorific.find()) return WHITESPACE_RE.matcher(honorific.group()).replaceAll(" ").trim();
        Matcher signoff = SIGNOFF_RE.matcher(body);
        if (signoff.find()) return signoff.group(1).trim();
        return null;
    }

    private String titleCase(String s) {
        StringBuilder sb = new StringBuilder(s);
        Matcher m = WORD_START_RE.matcher(s);
        while (m.find()) {
            int i = m.start();
            sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
        }
        return sb.toString();
    }

    private IntakeFieldDto field(String label, String value) {
        return (value != null && !value.isEmpty())
                ? new IntakeFieldDto(label, value, true)
                : new IntakeFieldDto(label, "Not found in the email", false);
    }

    private String firstMatch(Pattern p, String text) {
        Matcher m = p.matcher(text);
        return m.find() ? m.group() : null;
    }

    private String firstGroup(Pattern p, String text) {
        Matcher m = p.matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
