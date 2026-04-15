package com.traffic.model;

public enum TimingProfile {
    NORMAL("Normal traffic", 5000, 2000, 5000),
    MORNING_PEAK("Morning peak", 8000, 2500, 4500),
    EVENING_PEAK("Evening peak", 9000, 2500, 4500),
    SCHOOL_PEAK("School zone peak", 6500, 2500, 4000),
    EVENT_CONTROL("Event control", 10000, 3000, 5000);

    private final String label;
    private final long greenDuration;
    private final long yellowDuration;
    private final long redDuration;

    TimingProfile(String label, long greenDuration, long yellowDuration, long redDuration) {
        this.label = label;
        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;
        this.redDuration = redDuration;
    }

    public String getLabel() { return label; }
    public long getGreenDuration() { return greenDuration; }
    public long getYellowDuration() { return yellowDuration; }
    public long getRedDuration() { return redDuration; }

    public TimerConfig toTimerConfig() {
        return new TimerConfig(greenDuration, yellowDuration, redDuration);
    }
}
