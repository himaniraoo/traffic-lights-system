package com.traffic.model;

public enum TrafficDensity {
    LIGHT("Light", 0.90),
    MODERATE("Moderate", 1.00),
    HEAVY("Heavy", 1.25),
    JAMMED("Jammed", 1.55);

    private final String label;
    private final double greenMultiplier;

    TrafficDensity(String label, double greenMultiplier) {
        this.label = label;
        this.greenMultiplier = greenMultiplier;
    }

    public String getLabel() { return label; }
    public double getGreenMultiplier() { return greenMultiplier; }

    public static TrafficDensity fromVehicleCount(int vehicleCount) {
        if (vehicleCount >= 70) return JAMMED;
        if (vehicleCount >= 45) return HEAVY;
        if (vehicleCount >= 20) return MODERATE;
        return LIGHT;
    }
}
