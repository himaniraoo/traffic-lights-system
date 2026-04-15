package com.traffic.model;

import jakarta.validation.constraints.Min;

public class TimerConfig {

    @Min(value = 1000, message = "Green duration must be at least 1000ms")
    private long greenDuration;

    @Min(value = 500, message = "Yellow duration must be at least 500ms")
    private long yellowDuration;

    @Min(value = 1000, message = "Red duration must be at least 1000ms")
    private long redDuration;

    public TimerConfig() {
        this.greenDuration  = 5000;
        this.yellowDuration = 2000;
        this.redDuration    = 5000;
    }

    public TimerConfig(long greenDuration, long yellowDuration, long redDuration) {
        this.greenDuration  = greenDuration;
        this.yellowDuration = yellowDuration;
        this.redDuration    = redDuration;
    }

    public long getGreenDuration()  { return greenDuration; }
    public long getYellowDuration() { return yellowDuration; }
    public long getRedDuration()    { return redDuration; }

    public void setGreenDuration(long v)  { this.greenDuration  = v; }
    public void setYellowDuration(long v) { this.yellowDuration = v; }
    public void setRedDuration(long v)    { this.redDuration    = v; }

    public long getDurationFor(SignalState state) {
        return switch (state) {
            case GREEN  -> greenDuration;
            case YELLOW -> yellowDuration;
            case RED    -> redDuration;
        };
    }

    @Override
    public String toString() {
        return "TimerConfig{green=" + greenDuration + "ms, yellow=" + yellowDuration + "ms, red=" + redDuration + "ms}";
    }
}
