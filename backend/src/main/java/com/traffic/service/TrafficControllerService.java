package com.traffic.service;

import com.traffic.config.WebSocketConfig;
import com.traffic.factory.JunctionNetworkFactory;
import com.traffic.model.*;
import com.traffic.strategy.AutomaticSignalPlanStrategy;
import com.traffic.strategy.ManualSignalPlanStrategy;
import com.traffic.strategy.PrioritySignalPlanStrategy;
import com.traffic.strategy.SignalPlanStrategy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Service
public class TrafficControllerService {

    private static final Logger log = Logger.getLogger(TrafficControllerService.class.getName());

    // ── Formal state enum matching the state diagram ───────────────────────────
    public enum CycleState { IDLE, RUNNING, UPDATING, MANUAL_CONTROL, PRIORITY_CONTROL, STOPPED }

    private final SignalControllerService signalController;
    private final SimpMessagingTemplate   messagingTemplate;
    private final SignalPlanStrategy      automaticPlan = new AutomaticSignalPlanStrategy();
    private final SignalPlanStrategy      manualPlan    = new ManualSignalPlanStrategy();
    private final SignalPlanStrategy      priorityPlan  = new PrioritySignalPlanStrategy();
    private final Random                  random        = new Random();

    private final JunctionNetwork network      = JunctionNetworkFactory.createSouthEndNetwork();
    private       TimerConfig   timerConfig    = new TimerConfig();
    private final AtomicBoolean running        = new AtomicBoolean(false);
    private final Map<String, Long> phaseStartTimes = new HashMap<>();
    private       CycleState    cycleState     = CycleState.IDLE;
    private       SignalMode    signalMode     = SignalMode.AUTOMATIC;
    private       PriorityEventType priorityEventType = PriorityEventType.NONE;
    private       boolean       adaptiveTimingEnabled = true;
    private       boolean       densitySimulationEnabled = true;
    private       TimingProfile activeTimingProfile = TimingProfile.NORMAL;
    private       long          lastDensitySimulationAt = 0;
    private       String        priorityJunctionId;
    private       Direction     priorityDirection;

    public TrafficControllerService(SignalControllerService signalController,
                                    SimpMessagingTemplate messagingTemplate) {
        this.signalController  = signalController;
        this.messagingTemplate = messagingTemplate;
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    public String startCycle() {
        if (running.get()) return "Cycle already running";
        running.set(true);
        cycleState     = CycleState.RUNNING;
        signalMode     = SignalMode.AUTOMATIC;
        long now = System.currentTimeMillis();
        network.getIntersections().forEach(intersection -> {
            phaseStartTimes.put(intersection.getId(), now);
            automaticPlan.apply(intersection, signalController.getActiveDirection(intersection.getId()));
        });
        broadcast();
        log.info("CycleState -> RUNNING");
        activeTimingProfile = resolveTimingProfile();
        return "Automatic cycle started for " + network.size() + " varied junctions using " + activeTimingProfile.getLabel() + " timing";
    }

    public String stopCycle() {
        if (!running.get() && cycleState == CycleState.IDLE) return "Cycle not running";
        running.set(false);
        cycleState = CycleState.STOPPED;
        signalMode = SignalMode.AUTOMATIC;
        priorityEventType = PriorityEventType.NONE;
        network.resetAll();
        signalController.resetAllIndexes();
        broadcast();
        log.info("CycleState -> STOPPED");
        // Return to IDLE after stop (default state restored)
        cycleState = CycleState.IDLE;
        return "Cycle stopped — all lights RED";
    }

    // ── Scheduled tick every 500 ms ────────────────────────────────────────────

    @Scheduled(fixedRate = 500)
    public void tick() {
        if (!running.get()) return;

        if (signalMode != SignalMode.AUTOMATIC || priorityEventType != PriorityEventType.NONE) return;

        long now = System.currentTimeMillis();
        boolean changed = simulateTrafficDensityIfDue(now);
        activeTimingProfile = resolveTimingProfile();
        for (Intersection intersection : network.getIntersections()) {
            Direction activeDir = signalController.getActiveDirection(intersection.getId());
            TrafficLight activeLight = intersection.getLight(activeDir);
            SignalState currentState = activeLight.getCurrentState();
            long elapsed = now - phaseStartTimes.getOrDefault(intersection.getId(), now);
            long phaseDuration = getEffectiveDurationFor(currentState, intersection);

            if (elapsed >= phaseDuration) {
                signalController.advance(intersection);
                phaseStartTimes.put(intersection.getId(), now);
                changed = true;
            }
        }

        if (changed) broadcast();
    }

    // ── Timer config update — pauses cycle briefly (UPDATING state) ───────────

    public void updateTimerConfig(TimerConfig newConfig) {
        CycleState previous = cycleState;
        boolean wasRunning  = running.getAndSet(false);

        cycleState = CycleState.UPDATING;
        log.info("CycleState -> UPDATING");

        this.timerConfig = newConfig;

        if (wasRunning) {
            running.set(true);
            cycleState = previous;
            log.info("CycleState -> " + previous + " (resumed after config update)");
        } else {
            cycleState = CycleState.IDLE;
        }
    }

    private long getEffectiveDurationFor(SignalState state, Intersection intersection) {
        activeTimingProfile = resolveTimingProfile();
        TimerConfig effectiveConfig = adaptiveTimingEnabled ? activeTimingProfile.toTimerConfig() : timerConfig;
        long duration = effectiveConfig.getDurationFor(state);

        if (state == SignalState.GREEN && adaptiveTimingEnabled) {
            duration = Math.round(duration * intersection.getTrafficLoad().getGreenMultiplier());
            duration = Math.round(duration * intersection.getTrafficDensity().getGreenMultiplier());
        }

        return duration;
    }

    private boolean simulateTrafficDensityIfDue(long now) {
        if (!densitySimulationEnabled || now - lastDensitySimulationAt < 5000) return false;

        for (Intersection intersection : network.getIntersections()) {
            int drift = switch (intersection.getTrafficLoad()) {
                case LOW -> random.nextInt(9) - 4;
                case MEDIUM -> random.nextInt(13) - 5;
                case HIGH -> random.nextInt(17) - 6;
                case CRITICAL -> random.nextInt(21) - 7;
            };
            intersection.adjustVehicleCount(drift);
        }
        lastDensitySimulationAt = now;
        return true;
    }

    private TimingProfile resolveTimingProfile() {
        if (!adaptiveTimingEnabled) return TimingProfile.NORMAL;

        LocalTime now = LocalTime.now();
        if (!now.isBefore(LocalTime.of(7, 0)) && now.isBefore(LocalTime.of(10, 0))) {
            return TimingProfile.MORNING_PEAK;
        }
        if (!now.isBefore(LocalTime.of(15, 0)) && now.isBefore(LocalTime.of(16, 30))) {
            return TimingProfile.SCHOOL_PEAK;
        }
        if (!now.isBefore(LocalTime.of(17, 0)) && now.isBefore(LocalTime.of(20, 30))) {
            return TimingProfile.EVENING_PEAK;
        }
        return TimingProfile.NORMAL;
    }

    public String setAdaptiveTimingEnabled(boolean enabled) {
        adaptiveTimingEnabled = enabled;
        activeTimingProfile = resolveTimingProfile();
        return enabled
            ? "Adaptive peak-hour timing enabled. Active profile: " + activeTimingProfile.getLabel()
            : "Adaptive timing disabled. Manual timer configuration is active.";
    }

    public String setDensitySimulationEnabled(boolean enabled) {
        densitySimulationEnabled = enabled;
        return enabled
            ? "Traffic density simulation enabled. Vehicle counts will change automatically."
            : "Traffic density simulation disabled. Manual vehicle counts are active.";
    }

    public String updateTrafficDensity(TrafficDensityRequest request) {
        Intersection intersection = network.getIntersection(request.getJunctionId());
        if (intersection == null) return "Invalid junction for traffic density update.";

        densitySimulationEnabled = false;
        intersection.updateVehicleCount(request.getVehicleCount());
        broadcast();
        return "Updated " + intersection.getName() + " to " + intersection.getVehicleCount()
            + " vehicles (" + intersection.getTrafficDensity().getLabel() + " traffic).";
    }

    public String setSignalMode(SignalMode mode) {
        this.signalMode = mode;
        if (mode == SignalMode.MANUAL) {
            running.set(false);
            cycleState = CycleState.MANUAL_CONTROL;
            network.resetAll();
            broadcast();
            return "Manual mode enabled. Select a junction and direction to give GREEN.";
        }

        priorityEventType = PriorityEventType.NONE;
        running.set(true);
        cycleState = CycleState.RUNNING;
        activeTimingProfile = resolveTimingProfile();
        long now = System.currentTimeMillis();
        network.getIntersections().forEach(intersection -> phaseStartTimes.put(intersection.getId(), now));
        broadcast();
        return "Automatic mode enabled.";
    }

    public String applyManualSignal(ManualSignalRequest request) {
        Intersection intersection = network.getIntersection(request.getJunctionId());
        if (intersection == null || request.getDirection() == null) {
            return "Invalid manual signal request";
        }

        signalMode = SignalMode.MANUAL;
        running.set(false);
        cycleState = CycleState.MANUAL_CONTROL;
        manualPlan.apply(intersection, request.getDirection());
        signalController.setGreen(intersection, request.getDirection());
        broadcast();
        return "Manual GREEN applied at " + intersection.getName() + " for " + request.getDirection().getLabel();
    }

    public String applyPriority(PriorityRequest request) {
        if (request.getEventType() == PriorityEventType.NONE) {
            priorityEventType = PriorityEventType.NONE;
            priorityJunctionId = null;
            priorityDirection = null;
            cycleState = signalMode == SignalMode.MANUAL ? CycleState.MANUAL_CONTROL : CycleState.RUNNING;
            broadcast();
            return "Priority event cleared.";
        }

        Intersection intersection = network.getIntersection(request.getJunctionId());
        if (intersection == null || request.getDirection() == null) {
            return "Invalid priority request";
        }

        running.set(false);
        priorityEventType = request.getEventType();
        priorityJunctionId = request.getJunctionId();
        priorityDirection = request.getDirection();
        cycleState = CycleState.PRIORITY_CONTROL;
        priorityPlan.apply(intersection, request.getDirection());
        signalController.setGreen(intersection, request.getDirection());
        broadcast();
        return request.getEventType() + " priority enabled at " + intersection.getName() + " for " + request.getDirection().getLabel();
    }

    public String simulateConflictingGreenFault() {
        running.set(false);
        cycleState = CycleState.PRIORITY_CONTROL;
        signalMode = SignalMode.MANUAL;
        Intersection intersection = network.getIntersection("J1");
        if (intersection == null) return "Unable to simulate fault: J1 not found";

        intersection.resetAll();
        intersection.getLight(Direction.NORTH).setState(SignalState.GREEN);
        intersection.getLight(Direction.EAST).setState(SignalState.GREEN);
        broadcast();
        return "Simulated fault: J1 has NORTH and EAST GREEN at the same time.";
    }

    // ── WebSocket broadcast ────────────────────────────────────────────────────

    private void broadcast() {
        List<TrafficLight.TrafficLightDTO> payload = network.getAllLightDTOs();
        messagingTemplate.convertAndSend(WebSocketConfig.TOPIC_SIGNALS, payload);
    }

    // ── Accessors ──────────────────────────────────────────────────────────────

    public List<TrafficLight.TrafficLightDTO> getCurrentStatus() { return network.getAllLightDTOs(); }
    public TimerConfig  getTimerConfig()   { return timerConfig; }
    public TimerConfig  getEffectiveTimerConfig() { return adaptiveTimingEnabled ? activeTimingProfile.toTimerConfig() : timerConfig; }
    public boolean      isRunning()        { return running.get(); }
    public Intersection getIntersection()  { return network.getIntersections().iterator().next(); }
    public JunctionNetwork getNetwork()    { return network; }
    public CycleState   getCycleState()    { return cycleState; }
    public SignalMode   getSignalMode()    { return signalMode; }
    public PriorityEventType getPriorityEventType() { return priorityEventType; }
    public boolean      isAdaptiveTimingEnabled() { return adaptiveTimingEnabled; }
    public boolean      isDensitySimulationEnabled() { return densitySimulationEnabled; }
    public TimingProfile getActiveTimingProfile() {
        activeTimingProfile = resolveTimingProfile();
        return activeTimingProfile;
    }
    public String       getPriorityJunctionId() { return priorityJunctionId; }
    public Direction    getPriorityDirection() { return priorityDirection; }

    // kept for SystemManagerService reset
    public void forceStop() {
        running.set(false);
        cycleState = CycleState.IDLE;
        signalMode = SignalMode.AUTOMATIC;
        priorityEventType = PriorityEventType.NONE;
        network.resetAll();
        signalController.resetAllIndexes();
        broadcast();
    }
}
