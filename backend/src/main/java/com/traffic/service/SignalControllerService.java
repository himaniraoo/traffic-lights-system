package com.traffic.service;

import com.traffic.model.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class SignalControllerService {

    private static final Logger log = Logger.getLogger(SignalControllerService.class.getName());

    private static final Direction[] ROTATION = {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    private final Map<String, Integer> activeIndexes = new HashMap<>();

    public void advance(Intersection intersection) {
        int activeIndex = getActiveIndex(intersection.getId());
        Direction    activeDir   = ROTATION[activeIndex];
        TrafficLight activeLight = intersection.getLight(activeDir);
        SignalState  current     = activeLight.getCurrentState();

        log.fine("Advancing: direction=" + activeDir + " state=" + current);

        if (current == SignalState.GREEN) {
            activeLight.setState(SignalState.YELLOW);

        } else if (current == SignalState.YELLOW) {
            activeLight.setState(SignalState.RED);
            activeIndex = (activeIndex + 1) % ROTATION.length;
            activeIndexes.put(intersection.getId(), activeIndex);
            intersection.getLight(ROTATION[activeIndex]).setState(SignalState.GREEN);

        } else {
            activeLight.setState(SignalState.GREEN);
        }
    }

    public void setGreen(Intersection intersection, Direction direction) {
        intersection.activateOnly(direction, SignalState.GREEN);
        for (int i = 0; i < ROTATION.length; i++) {
            if (ROTATION[i] == direction) {
                activeIndexes.put(intersection.getId(), i);
                return;
            }
        }
    }

    public boolean hasConflictingGreens(Intersection intersection) {
        List<TrafficLight> greens = intersection.getAllLights().values().stream()
            .filter(l -> l.getCurrentState() == SignalState.GREEN)
            .toList();
        return greens.size() > 1;
    }

    public int getActiveIndex(String junctionId) {
        return activeIndexes.getOrDefault(junctionId, 0);
    }

    public Direction getActiveDirection(String junctionId) {
        return ROTATION[getActiveIndex(junctionId)];
    }

    public void resetIndex(String junctionId) {
        activeIndexes.put(junctionId, 0);
    }

    public void resetAllIndexes() {
        activeIndexes.clear();
    }
}
