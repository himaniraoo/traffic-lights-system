package com.traffic.model;

public class PriorityRequest {
    private PriorityEventType eventType = PriorityEventType.NONE;
    private String junctionId;
    private Direction direction;

    public PriorityEventType getEventType() { return eventType; }
    public String getJunctionId() { return junctionId; }
    public Direction getDirection() { return direction; }

    public void setEventType(PriorityEventType eventType) { this.eventType = eventType; }
    public void setJunctionId(String junctionId) { this.junctionId = junctionId; }
    public void setDirection(Direction direction) { this.direction = direction; }
}
