package com.traffic.service;

import com.traffic.model.DiagnosticsResult;
import com.traffic.model.TimerConfig;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class SystemManagerService {

    private static final Logger log = Logger.getLogger(SystemManagerService.class.getName());

    // ── Formal state enum matching the SystemManager state diagram ─────────────
    public enum ManagerState { UNINITIALIZED, INITIALIZED, RUNNING, FAULT }

    private final TrafficControllerService trafficController;
    private final DiagnosticsService       diagnosticsService;

    private ManagerState managerState = ManagerState.INITIALIZED;

    public SystemManagerService(TrafficControllerService trafficController,
                                DiagnosticsService diagnosticsService) {
        this.trafficController  = trafficController;
        this.diagnosticsService = diagnosticsService;
        log.info("ManagerState -> INITIALIZED");
    }

    // ── Called when the signal cycle starts (INITIALIZED → RUNNING) ────────────

    public void notifyCycleStarted() {
        managerState = ManagerState.RUNNING;
        log.info("ManagerState -> RUNNING");
    }

    // ── Called when a fault is detected (RUNNING → FAULT) ─────────────────────

    public void notifyFault(String reason) {
        managerState = ManagerState.FAULT;
        log.warning("ManagerState -> FAULT: " + reason);
    }

    // ── Reset: returns system to INITIALIZED, clears any FAULT ────────────────

    public String resetToDefaults() {
        trafficController.forceStop();
        trafficController.updateTimerConfig(new TimerConfig());
        managerState = ManagerState.INITIALIZED;
        log.info("ManagerState -> INITIALIZED (after reset)");
        return "System reset to defaults. All lights are RED. Timings restored.";
    }

    // ── Diagnostics: if errors found, transition to FAULT ─────────────────────

    public DiagnosticsResult runDiagnostics() {
        DiagnosticsResult result = diagnosticsService.runDiagnostics(
            trafficController.getNetwork(),
            trafficController.getTimerConfig()
        );
        if (!result.isHealthy()) {
            notifyFault("Diagnostics failed: " + result.getErrors());
        }
        return result;
    }

    // ── Status ─────────────────────────────────────────────────────────────────

    public SystemStatus getSystemStatus() {
        return new SystemStatus(
            trafficController.isRunning(),
            trafficController.getCycleState().name(),
            managerState.name(),
            trafficController.getSignalMode().name(),
            trafficController.getPriorityEventType().name(),
            trafficController.getTimerConfig(),
            trafficController.getNetwork().size(),
            trafficController.getCurrentStatus().size()
        );
    }

    public ManagerState getManagerState() { return managerState; }

    public record SystemStatus(
        boolean     cycleRunning,
        String      cycleState,
        String      managerState,
        String      signalMode,
        String      priorityEvent,
        TimerConfig timerConfig,
        int         junctionCount,
        int         activeLights
    ) {}
}
