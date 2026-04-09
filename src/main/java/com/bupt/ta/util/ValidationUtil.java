package com.bupt.ta.util;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\d{11}$");
    }

    public static boolean isPositiveInteger(String value) {
        return value != null && value.matches("^\\d+$") && Integer.parseInt(value) > 0;
    }
}
