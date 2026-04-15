package com.traffic.model;

public class TrafficLight {

    private final String    id;
    private final String    junctionId;
    private final String    junctionName;
    private final Direction direction;
    private SignalState     currentState;
    private long            lastTransitionTime;

    public TrafficLight(Direction direction) {
        this("MAIN", "Central Junction", direction);
    }

    public TrafficLight(String junctionId, String junctionName, Direction direction) {
        this.id                 = junctionId + "-" + direction.name();
        this.junctionId         = junctionId;
        this.junctionName       = junctionName;
        this.direction          = direction;
        this.currentState       = SignalState.RED;
        this.lastTransitionTime = System.currentTimeMillis();
    }

    public String      getId()                 { return id; }
    public String      getJunctionId()         { return junctionId; }
    public String      getJunctionName()       { return junctionName; }
    public Direction   getDirection()          { return direction; }
    public SignalState getCurrentState()       { return currentState; }
    public long        getLastTransitionTime() { return lastTransitionTime; }

    public void setState(SignalState newState) {
        this.currentState       = newState;
        this.lastTransitionTime = System.currentTimeMillis();
    }

    public TrafficLightDTO toDTO() {
        return new TrafficLightDTO(
            id,
            junctionId,
            junctionName,
            direction.name(),
            direction.getLabel(),
            currentState.name(),
            currentState.getColorHex(),
            lastTransitionTime
        );
    }

    public record TrafficLightDTO(
        String id,
        String junctionId,
        String junctionName,
        String directionKey,
        String direction,
        String state,
        String colorHex,
        long   lastTransitionTime
    ) {}
}
