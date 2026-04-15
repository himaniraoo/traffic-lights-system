package com.traffic.model;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticsResult {

    private boolean      healthy;
    private List<String> errors;
    private long         checkedAt;

    public DiagnosticsResult() {
        this.healthy   = true;
        this.errors    = new ArrayList<>();
        this.checkedAt = System.currentTimeMillis();
    }

    public boolean      isHealthy()   { return healthy; }
    public List<String> getErrors()   { return errors; }
    public long         getCheckedAt(){ return checkedAt; }

    public void setHealthy(boolean healthy)    { this.healthy   = healthy; }
    public void setErrors(List<String> errors) { this.errors    = errors; }
    public void setCheckedAt(long checkedAt)   { this.checkedAt = checkedAt; }

    public void addError(String message) {
        this.healthy = false;
        this.errors.add(message);
    }
}
