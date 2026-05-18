package com.bupt.ta.dto;

import java.util.ArrayList;
import java.util.List;

public class ApplicantProfileCompleteness {
    private int completedFieldCount;
    private int totalFieldCount;
    private int completionPercentage;
    private List<String> missingItems = new ArrayList<>();

    public int getCompletedFieldCount() {
        return completedFieldCount;
    }

    public void setCompletedFieldCount(int completedFieldCount) {
        this.completedFieldCount = completedFieldCount;
    }

    public int getTotalFieldCount() {
        return totalFieldCount;
    }

    public void setTotalFieldCount(int totalFieldCount) {
        this.totalFieldCount = totalFieldCount;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public List<String> getMissingItems() {
        return missingItems;
    }

    public void setMissingItems(List<String> missingItems) {
        this.missingItems = missingItems;
    }

    public boolean isComplete() {
        return missingItems == null || missingItems.isEmpty();
    }

    public String getCompletionLabel() {
        return "Profile " + completionPercentage + "% complete";
    }
}
