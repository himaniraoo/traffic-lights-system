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
            checkTrafficDensity(intersection, result);
        });
        checkTimerConfig(config, result);

        if (network.size() < 6) {
            result.addError("Network must contain at least six varied junctions for the expanded multi-junction case.");
        } else {
            result.addCheck("Network variety check passed: " + network.size() + " junctions configured");
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
        } else {
            result.addCheck(intersection.getName() + ": green conflict check passed (" + greens.size() + " green signal)");
        }
    }

    private void checkForNullStates(Intersection intersection, DiagnosticsResult result) {
        boolean hasNullState = false;
        intersection.getAllLights().forEach((dir, light) -> {
            if (light.getCurrentState() == null) {
                result.addError("NULL state detected for direction: " + dir.getLabel());
            }
        });
        for (TrafficLight light : intersection.getAllLights().values()) {
            if (light.getCurrentState() == null) {
                hasNullState = true;
                break;
            }
        }
        if (!hasNullState) {
            result.addCheck(intersection.getName() + ": all signal states are valid");
        }
    }

    private void checkTimerConfig(TimerConfig config, DiagnosticsResult result) {
        boolean timerOk = true;
        if (config.getGreenDuration() < 1000) {
            result.addError("Green duration too short (min 1000ms): " + config.getGreenDuration());
            timerOk = false;
        }
        if (config.getYellowDuration() < 500) {
            result.addError("Yellow duration too short (min 500ms): " + config.getYellowDuration());
            timerOk = false;
        }
        if (config.getRedDuration() < 1000) {
            result.addError("Red duration too short (min 1000ms): " + config.getRedDuration());
            timerOk = false;
        }
        if (timerOk) {
            result.addCheck("Timer configuration valid: green=" + config.getGreenDuration()
                + "ms, yellow=" + config.getYellowDuration()
                + "ms, red=" + config.getRedDuration() + "ms");
        }
    }

    private void checkTrafficDensity(Intersection intersection, DiagnosticsResult result) {
        if (intersection.getVehicleCount() < 0 || intersection.getVehicleCount() > 120) {
            result.addError("Invalid vehicle count at " + intersection.getName() + ": " + intersection.getVehicleCount());
            return;
        }
        result.addCheck(intersection.getName() + ": density check passed ("
            + intersection.getVehicleCount() + " vehicles, "
            + intersection.getTrafficDensity().getLabel() + ")");
    }
}
