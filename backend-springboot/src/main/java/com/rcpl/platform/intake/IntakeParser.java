package com.rcpl.platform.intake;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rcpl.platform.intake.IntakeDtos.IntakeFieldDto;
import com.rcpl.platform.intake.IntakeDtos.ParseResult;
import org.springframework.stereotype.Component;

/**
 * Deterministic, rule-based extraction of intake fields from an email's subject + body.
 * NO LLM / AI — labeled-field heuristics and regexes only, so the same input always yields the
 * same output and it works with no network access.
 */
@Component
public class IntakeParser {

    /** Canonical field -> label-matching regex (case-insensitive, "Label : value" lines). */
    private static final Map<String, Pattern> LABELS = new LinkedHashMap<>();
    static {
        LABELS.put("Distributor Name", label("(distributor\\s*name|firm\\s*name|name|party\\s*name)"));
        LABELS.put("Town", label("(town|city|location)"));
        LABELS.put("Monthly Turnover", label("(monthly\\s*turnover|turnover|monthly\\s*sales)"));
        LABELS.put("Coverage Outlets", label("(coverage\\s*outlets|outlets|no\\.?\\s*of\\s*outlets|retail\\s*outlets)"));
        LABELS.put("GST", label("(gst(\\s*no\\.?|in)?)"));
        LABELS.put("PAN", label("(pan(\\s*no\\.?|card)?)"));
        LABELS.put("Phone", label("(phone|mobile|contact\\s*(no\\.?|number)?)"));
        LABELS.put("Email", label("(e-?mail)"));
        LABELS.put("DB Category", label("(db\\s*category|category|distributor\\s*type)"));
    }

    /** The fields whose presence counts toward the confidence score. */
    private static final List<String> EXPECTED = List.of(
            "Distributor Name", "Town", "Monthly Turnover", "Coverage Outlets", "GST", "PAN");

    private static final Pattern GST_RE = Pattern.compile("\\b\\d{2}[A-Z]{5}\\d{4}[A-Z][A-Z0-9]Z[A-Z0-9]\\b");
    private static final Pattern PAN_RE = Pattern.compile("\\b[A-Z]{5}\\d{4}[A-Z]\\b");
    private static final Pattern EMAIL_RE = Pattern.compile("[\\w.+-]+@[\\w-]+\\.[\\w.-]+");
    private static final Pattern PHONE_RE = Pattern.compile("\\b(?:\\+?91[-\\s]?)?[6-9]\\d{9}\\b");

    private static Pattern label(String group) {
        return Pattern.compile("(?im)^\\s*" + group + "\\s*[:\\-]\\s*(.+?)\\s*$");
    }

    public ParseResult parse(String subject, String body) {
        String text = (subject == null ? "" : subject + "\n") + (body == null ? "" : body);
        Map<String, String> found = new LinkedHashMap<>();

        // 1) Labeled "Field: value" lines.
        for (Map.Entry<String, Pattern> e : LABELS.entrySet()) {
            Matcher m = e.getValue().matcher(text);
            if (m.find()) {
                found.putIfAbsent(e.getKey(), m.group(m.groupCount()).trim());
            }
        }
        // 2) Free-text pattern fallbacks for the strongly-typed identifiers.
        firstMatch(GST_RE, text).ifPresent(v -> found.putIfAbsent("GST", v));
        firstMatch(PAN_RE, text).ifPresent(v -> found.putIfAbsent("PAN", v));
        firstMatch(EMAIL_RE, text).ifPresent(v -> found.putIfAbsent("Email", v));
        firstMatch(PHONE_RE, text).ifPresent(v -> found.putIfAbsent("Phone", v));

        List<IntakeFieldDto> fields = new ArrayList<>();
        for (Map.Entry<String, String> e : found.entrySet()) {
            fields.add(new IntakeFieldDto(e.getKey(), e.getValue(), isValid(e.getKey(), e.getValue())));
        }

        long matchedExpected = EXPECTED.stream().filter(found::containsKey).count();
        BigDecimal confidence = BigDecimal.valueOf(matchedExpected)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(EXPECTED.size()), 0, RoundingMode.HALF_UP);

        String partnerType = guessPartnerType(text);
        String name = found.getOrDefault("Distributor Name", "New intake");
        String town = found.getOrDefault("Town", "");
        String summary = "%s%s — %d field(s) extracted".formatted(name, town.isBlank() ? "" : " · " + town, fields.size());

        return new ParseResult(partnerType, summary, confidence, fields);
    }

    private boolean isValid(String key, String value) {
        return switch (key) {
            case "GST" -> GST_RE.matcher(value).find();
            case "PAN" -> PAN_RE.matcher(value).find();
            case "Email" -> EMAIL_RE.matcher(value).find();
            case "Phone" -> PHONE_RE.matcher(value).find();
            default -> value != null && !value.isBlank();
        };
    }

    private String guessPartnerType(String text) {
        String t = text.toLowerCase();
        if (t.contains("vendor")) return "vendor";
        if (t.contains("logistic")) return "logistics";
        if (t.contains("co-pack") || t.contains("copack")) return "copacker";
        return "distributor";
    }

    private java.util.Optional<String> firstMatch(Pattern p, String text) {
        Matcher m = p.matcher(text);
        return m.find() ? java.util.Optional.of(m.group()) : java.util.Optional.empty();
    }
}
