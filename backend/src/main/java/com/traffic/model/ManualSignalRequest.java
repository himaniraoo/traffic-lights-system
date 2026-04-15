package com.traffic.model;

public class ManualSignalRequest {
    private String junctionId;
    private Direction direction;

    public String getJunctionId() { return junctionId; }
    public Direction getDirection() { return direction; }

    public void setJunctionId(String junctionId) { this.junctionId = junctionId; }
    public void setDirection(Direction direction) { this.direction = direction; }
}
