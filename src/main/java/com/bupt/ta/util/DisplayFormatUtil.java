package com.bupt.ta.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class DisplayFormatUtil {
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE =
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private DisplayFormatUtil() {
    }

    public static String formatDateTime(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }

        String trimmedValue = value.trim();
        try {
            return LocalDateTime.parse(trimmedValue).format(DISPLAY_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            return formatDateFallback(trimmedValue);
        }
    }

    public static String formatEnumName(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }

        String[] words = value.trim().toLowerCase(Locale.ROOT).split("[_\\s]+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                builder.append(word.substring(1));
            }
        }
        return builder.length() == 0 ? value : builder.toString();
    }

    private static String formatDateFallback(String value) {
        try {
            return LocalDate.parse(value).format(DISPLAY_DATE);
        } catch (DateTimeParseException ignored) {
            // Keep unexpected legacy values visible rather than hiding data.
            return value;
        }
    }
}
