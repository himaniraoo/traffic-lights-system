package com.traffic.model;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticsResult {

    private boolean      healthy;
    private List<String> errors;
    private List<String> checks;
    private long         checkedAt;

    public DiagnosticsResult() {
        this.healthy   = true;
        this.errors    = new ArrayList<>();
        this.checks    = new ArrayList<>();
        this.checkedAt = System.currentTimeMillis();
    }

    public boolean      isHealthy()   { return healthy; }
    public List<String> getErrors()   { return errors; }
    public List<String> getChecks()   { return checks; }
    public long         getCheckedAt(){ return checkedAt; }

    public void setHealthy(boolean healthy)    { this.healthy   = healthy; }
    public void setErrors(List<String> errors) { this.errors    = errors; }
    public void setChecks(List<String> checks) { this.checks    = checks; }
    public void setCheckedAt(long checkedAt)   { this.checkedAt = checkedAt; }

    public void addError(String message) {
        this.healthy = false;
        this.errors.add(message);
    }

    public void addCheck(String message) {
        this.checks.add(message);
    }
}
