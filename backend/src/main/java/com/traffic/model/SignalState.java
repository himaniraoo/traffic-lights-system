package com.traffic.model;

/**
 * SignalState - Represents the possible states of a traffic light.
 * Used by the State design pattern for managing signal transitions.
 */
public enum SignalState {
    RED("red", "#FF3B30"),
    YELLOW("yellow", "#FFD60A"),
    GREEN("green", "#30D158");

    private final String label;
    private final String colorHex;

    SignalState(String label, String colorHex) {
        this.label = label;
        this.colorHex = colorHex;
    }

    public String getLabel() { return label; }
    public String getColorHex() { return colorHex; }

    /**
     * Returns the next state in the standard traffic light cycle.
     */
    public SignalState next() {
        return switch (this) {
            case GREEN  -> YELLOW;
            case YELLOW -> RED;
            case RED    -> GREEN;
        };
    }
}
