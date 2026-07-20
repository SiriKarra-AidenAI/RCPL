package com.rcpl.platform.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/** Human date/time labels matching the frontend's formats (store.ts auditStamp / dateStamp). */
public final class DateLabels {

    private DateLabels() {}

    private static final String[] MONTHS =
            {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static final DateTimeFormatter DATE_STAMP_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    /** e.g. "6 Jul, 14:30". */
    public static String auditStamp() {
        LocalDateTime d = LocalDateTime.now(ZoneId.systemDefault());
        return String.format("%d %s, %02d:%02d", d.getDayOfMonth(), MONTHS[d.getMonthValue() - 1],
                d.getHour(), d.getMinute());
    }

    /** e.g. "6 Jul 2026". */
    public static String dateStamp() {
        LocalDateTime d = LocalDateTime.now(ZoneId.systemDefault());
        return String.format("%d %s %d", d.getDayOfMonth(), MONTHS[d.getMonthValue() - 1], d.getYear());
    }

    /** Days elapsed since a dateStamp()-formatted label (e.g. "19 Jun 2026"); 0 if unparsable. */
    public static long daysSince(String label) {
        if (label == null || label.isBlank()) return 0;
        try {
            LocalDate then = LocalDate.parse(label.trim(), DATE_STAMP_FORMAT);
            return ChronoUnit.DAYS.between(then, LocalDate.now(ZoneId.systemDefault()));
        } catch (Exception e) {
            return 0;
        }
    }
}
