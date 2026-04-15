package com.traffic.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Intersection - Groups all four TrafficLight objects (one per Direction).
 * Acts as the root model object. Uses the Factory pattern to create lights.
 * Singleton bean managed by Spring.
 */
public class Intersection {

    private final String id;
    private final String name;
    private final JunctionType junctionType;
    private final TrafficLoad trafficLoad;
    private final String corridorRole;
    private int vehicleCount;
    private TrafficDensity trafficDensity;
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);

    /**
     * Factory-style initialisation: creates one TrafficLight per Direction.
     */
    public Intersection() {
        this("MAIN", "Central Junction", JunctionType.FOUR_WAY, TrafficLoad.MEDIUM, "General traffic");
    }

    public Intersection(String id, String name) {
        this(id, name, JunctionType.FOUR_WAY, TrafficLoad.MEDIUM, "General traffic");
    }

    public Intersection(String id, String name, JunctionType junctionType, TrafficLoad trafficLoad, String corridorRole) {
        this.id = id;
        this.name = name;
        this.junctionType = junctionType;
        this.trafficLoad = trafficLoad;
        this.corridorRole = corridorRole;
        this.vehicleCount = defaultVehicleCount(trafficLoad);
        this.trafficDensity = TrafficDensity.fromVehicleCount(vehicleCount);
        for (Direction dir : Direction.values()) {
            lights.put(dir, new TrafficLight(id, name, junctionType, trafficLoad, corridorRole, dir));
        }
        syncTrafficDensityToLights();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public JunctionType getJunctionType() { return junctionType; }
    public TrafficLoad getTrafficLoad() { return trafficLoad; }
    public String getCorridorRole() { return corridorRole; }
    public int getVehicleCount() { return vehicleCount; }
    public TrafficDensity getTrafficDensity() { return trafficDensity; }

    public void updateVehicleCount(int vehicleCount) {
        this.vehicleCount = Math.max(0, Math.min(vehicleCount, 120));
        this.trafficDensity = TrafficDensity.fromVehicleCount(this.vehicleCount);
        syncTrafficDensityToLights();
    }

    public void adjustVehicleCount(int delta) {
        updateVehicleCount(vehicleCount + delta);
    }

    public TrafficLight getLight(Direction direction) {
        return lights.get(direction);
    }

    public Map<Direction, TrafficLight> getAllLights() {
        return lights;
    }

    /**
     * Returns all lights as DTOs (used for REST and WebSocket responses).
     */
    public List<TrafficLight.TrafficLightDTO> getAllLightDTOs() {
        return lights.values()
                     .stream()
                     .map(TrafficLight::toDTO)
                     .collect(Collectors.toList());
    }

    /**
     * Resets all lights to RED (used by SystemManager for fault reset).
     */
    public void resetAll() {
        lights.values().forEach(l -> l.setState(SignalState.RED));
    }

    public void activateOnly(Direction direction, SignalState state) {
        resetAll();
        lights.get(direction).setState(state);
    }

    private void syncTrafficDensityToLights() {
        lights.values().forEach(light -> light.updateTrafficDensity(vehicleCount, trafficDensity));
    }

    private int defaultVehicleCount(TrafficLoad trafficLoad) {
        return switch (trafficLoad) {
            case LOW -> 14;
            case MEDIUM -> 30;
            case HIGH -> 52;
            case CRITICAL -> 78;
        };
    }
}
