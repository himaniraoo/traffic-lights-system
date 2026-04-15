package com.traffic.model;

public enum TrafficLoad {
    LOW(0.85),
    MEDIUM(1.0),
    HIGH(1.25),
    CRITICAL(1.45);

    private final double greenMultiplier;

    TrafficLoad(double greenMultiplier) {
        this.greenMultiplier = greenMultiplier;
    }

    public double getGreenMultiplier() {
        return greenMultiplier;
    }
}
