package com.traffic.model;

public enum JunctionType {
    FOUR_WAY("Four-way intersection"),
    T_JUNCTION("T-junction"),
    ROUNDABOUT_APPROACH("Roundabout approach"),
    PEDESTRIAN_CROSSING("Pedestrian-heavy crossing"),
    BUS_CORRIDOR("Bus corridor junction"),
    SCHOOL_ZONE("School zone");

    private final String label;

    JunctionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
