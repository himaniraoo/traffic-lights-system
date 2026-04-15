package com.traffic.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Composite model: a traffic network contains many intersections; each
 * intersection contains many traffic lights.
 */
public class JunctionNetwork {

    private final Map<String, Intersection> intersections = new LinkedHashMap<>();

    public void addIntersection(Intersection intersection) {
        intersections.put(intersection.getId(), intersection);
    }

    public Intersection getIntersection(String junctionId) {
        return intersections.get(junctionId);
    }

    public Collection<Intersection> getIntersections() {
        return intersections.values();
    }

    public List<TrafficLight.TrafficLightDTO> getAllLightDTOs() {
        return intersections.values().stream()
            .flatMap(intersection -> intersection.getAllLightDTOs().stream())
            .toList();
    }

    public void resetAll() {
        intersections.values().forEach(Intersection::resetAll);
    }

    public int size() {
        return intersections.size();
    }
}
