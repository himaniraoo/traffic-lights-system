package com.traffic.service;

import com.traffic.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class DiagnosticsService {

    private static final Logger log = Logger.getLogger(DiagnosticsService.class.getName());

    public DiagnosticsResult runDiagnostics(Intersection intersection, TimerConfig config) {
        DiagnosticsResult result = new DiagnosticsResult();

        checkForConflictingGreens(intersection, result);
        checkForNullStates(intersection, result);
        checkTimerConfig(config, result);

        if (result.isHealthy()) {
            log.info("Diagnostics PASSED — all checks OK");
        } else {
            log.warning("Diagnostics FAILED — errors: " + result.getErrors());
        }

        return result;
    }

    public DiagnosticsResult runDiagnostics(JunctionNetwork network, TimerConfig config) {
        DiagnosticsResult result = new DiagnosticsResult();

        network.getIntersections().forEach(intersection -> {
            checkForConflictingGreens(intersection, result);
            checkForNullStates(intersection, result);
        });
        checkTimerConfig(config, result);

        if (network.size() < 4) {
            result.addError("Network must contain at least four junctions for the multi-junction case.");
        }

        if (result.isHealthy()) {
            log.info("Network diagnostics PASSED — all checks OK");
        } else {
            log.warning("Network diagnostics FAILED — errors: " + result.getErrors());
        }

        return result;
    }

    private void checkForConflictingGreens(Intersection intersection, DiagnosticsResult result) {
        List<TrafficLight> greens = intersection.getAllLights().values().stream()
            .filter(l -> l.getCurrentState() == SignalState.GREEN)
            .toList();

        if (greens.size() > 1) {
            StringBuilder dirs = new StringBuilder();
            for (int i = 0; i < greens.size(); i++) {
                if (i > 0) dirs.append(", ");
                dirs.append(greens.get(i).getDirection().getLabel());
            }
            result.addError("Conflicting GREEN signals at " + intersection.getName() + ": " + dirs);
        }
    }

    private void checkForNullStates(Intersection intersection, DiagnosticsResult result) {
        intersection.getAllLights().forEach((dir, light) -> {
            if (light.getCurrentState() == null) {
                result.addError("NULL state detected for direction: " + dir.getLabel());
            }
        });
    }

    private void checkTimerConfig(TimerConfig config, DiagnosticsResult result) {
        if (config.getGreenDuration() < 1000) {
            result.addError("Green duration too short (min 1000ms): " + config.getGreenDuration());
        }
        if (config.getYellowDuration() < 500) {
            result.addError("Yellow duration too short (min 500ms): " + config.getYellowDuration());
        }
        if (config.getRedDuration() < 1000) {
            result.addError("Red duration too short (min 1000ms): " + config.getRedDuration());
        }
    }
}
