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
    private final Map<Direction, TrafficLight> lights = new EnumMap<>(Direction.class);

    /**
     * Factory-style initialisation: creates one TrafficLight per Direction.
     */
    public Intersection() {
        this("MAIN", "Central Junction");
    }

    public Intersection(String id, String name) {
        this.id = id;
        this.name = name;
        for (Direction dir : Direction.values()) {
            lights.put(dir, new TrafficLight(id, name, dir));
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }

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
}
