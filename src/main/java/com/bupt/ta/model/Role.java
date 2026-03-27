package com.bupt.ta.model;

public enum Role {
    TA_APPLICANT("TA Applicant"),
    MODULE_ORGANISER("Module Organiser"),
    ADMINISTRATOR("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
