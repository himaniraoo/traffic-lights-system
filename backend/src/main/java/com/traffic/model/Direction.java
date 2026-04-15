package com.traffic.model;

/**
 * Direction - Represents the four cardinal directions at the intersection.
 * Each direction has its own TrafficLight instance.
 */
public enum Direction {
    NORTH("North"),
    SOUTH("South"),
    EAST("East"),
    WEST("West");

    private final String label;

    Direction(String label) { this.label = label; }

    public String getLabel() { return label; }
}
