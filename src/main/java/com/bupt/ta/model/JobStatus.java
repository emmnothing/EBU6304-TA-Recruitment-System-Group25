package com.bupt.ta.model;

public enum JobStatus {
    OPEN,
    CLOSED,
    CLOSED_MANUAL,
    CLOSED_DEADLINE,
    CLOSED_FILLED;

    public boolean isOpen() {
        return this == OPEN;
    }

    public boolean isClosed() {
        return this != OPEN;
    }
}
