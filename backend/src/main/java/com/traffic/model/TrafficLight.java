package com.traffic.model;

public class TrafficLight {

    private final String    id;
    private final String    junctionId;
    private final String    junctionName;
    private final JunctionType junctionType;
    private final TrafficLoad trafficLoad;
    private final String    corridorRole;
    private final Direction direction;
    private int             vehicleCount;
    private TrafficDensity  trafficDensity;
    private SignalState     currentState;
    private long            lastTransitionTime;

    public TrafficLight(Direction direction) {
        this("MAIN", "Central Junction", JunctionType.FOUR_WAY, TrafficLoad.MEDIUM, "General traffic", direction);
    }

    public TrafficLight(String junctionId, String junctionName, Direction direction) {
        this(junctionId, junctionName, JunctionType.FOUR_WAY, TrafficLoad.MEDIUM, "General traffic", direction);
    }

    public TrafficLight(String junctionId, String junctionName, JunctionType junctionType,
                        TrafficLoad trafficLoad, String corridorRole, Direction direction) {
        this.id                 = junctionId + "-" + direction.name();
        this.junctionId         = junctionId;
        this.junctionName       = junctionName;
        this.junctionType       = junctionType;
        this.trafficLoad        = trafficLoad;
        this.corridorRole       = corridorRole;
        this.direction          = direction;
        this.vehicleCount       = 0;
        this.trafficDensity     = TrafficDensity.LIGHT;
        this.currentState       = SignalState.RED;
        this.lastTransitionTime = System.currentTimeMillis();
    }

    public String      getId()                 { return id; }
    public String      getJunctionId()         { return junctionId; }
    public String      getJunctionName()       { return junctionName; }
    public JunctionType getJunctionType()      { return junctionType; }
    public TrafficLoad getTrafficLoad()        { return trafficLoad; }
    public String      getCorridorRole()       { return corridorRole; }
    public int         getVehicleCount()       { return vehicleCount; }
    public TrafficDensity getTrafficDensity()  { return trafficDensity; }
    public Direction   getDirection()          { return direction; }
    public SignalState getCurrentState()       { return currentState; }
    public long        getLastTransitionTime() { return lastTransitionTime; }

    public void setState(SignalState newState) {
        this.currentState       = newState;
        this.lastTransitionTime = System.currentTimeMillis();
    }

    public void updateTrafficDensity(int vehicleCount, TrafficDensity trafficDensity) {
        this.vehicleCount = vehicleCount;
        this.trafficDensity = trafficDensity;
    }

    public TrafficLightDTO toDTO() {
        return new TrafficLightDTO(
            id,
            junctionId,
            junctionName,
            junctionType.getLabel(),
            trafficLoad.name(),
            corridorRole,
            vehicleCount,
            trafficDensity.name(),
            trafficDensity.getLabel(),
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
        String junctionType,
        String trafficLoad,
        String corridorRole,
        int vehicleCount,
        String trafficDensity,
        String trafficDensityLabel,
        String directionKey,
        String direction,
        String state,
        String colorHex,
        long   lastTransitionTime
    ) {}
}
