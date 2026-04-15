package com.traffic.controller;

import com.traffic.model.*;
import com.traffic.service.SystemManagerService;
import com.traffic.service.TrafficControllerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/signals")
public class TrafficController {

    private final TrafficControllerService trafficControllerService;
    private final SystemManagerService     systemManagerService;

    public TrafficController(TrafficControllerService trafficControllerService,
                             SystemManagerService systemManagerService) {
        this.trafficControllerService = trafficControllerService;
        this.systemManagerService     = systemManagerService;
    }

    @GetMapping("/status")
    public ResponseEntity<List<TrafficLight.TrafficLightDTO>> getStatus() {
        return ResponseEntity.ok(trafficControllerService.getCurrentStatus());
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startCycle() {
        String message = trafficControllerService.startCycle();
        // notify SystemManager so it transitions INITIALIZED → RUNNING
        if (trafficControllerService.isRunning()) {
            systemManagerService.notifyCycleStarted();
        }
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stopCycle() {
        String message = trafficControllerService.stopCycle();
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/timer")
    public ResponseEntity<TimerConfig> getTimerConfig() {
        return ResponseEntity.ok(trafficControllerService.getTimerConfig());
    }

    @PutMapping("/timer")
    public ResponseEntity<Map<String, Object>> updateTimerConfig(@Valid @RequestBody TimerConfig config) {
        trafficControllerService.updateTimerConfig(config);
        return ResponseEntity.ok(Map.of(
            "message", "Timer config updated successfully",
            "config",  config
        ));
    }

    @GetMapping("/running")
    public ResponseEntity<Map<String, Boolean>> isRunning() {
        return ResponseEntity.ok(Map.of("running", trafficControllerService.isRunning()));
    }

    @GetMapping("/state")
    public ResponseEntity<Map<String, String>> getCycleState() {
        return ResponseEntity.ok(Map.of("cycleState", trafficControllerService.getCycleState().name()));
    }

    @GetMapping("/mode")
    public ResponseEntity<Map<String, String>> getSignalMode() {
        return ResponseEntity.ok(Map.of(
            "mode", trafficControllerService.getSignalMode().name(),
            "priorityEvent", trafficControllerService.getPriorityEventType().name()
        ));
    }

    @PostMapping("/mode/{mode}")
    public ResponseEntity<Map<String, String>> setSignalMode(@PathVariable SignalMode mode) {
        String message = trafficControllerService.setSignalMode(mode);
        return ResponseEntity.ok(Map.of("message", message, "mode", mode.name()));
    }

    @PostMapping("/manual")
    public ResponseEntity<Map<String, String>> applyManualSignal(@RequestBody ManualSignalRequest request) {
        String message = trafficControllerService.applyManualSignal(request);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/priority")
    public ResponseEntity<Map<String, String>> applyPriority(@RequestBody PriorityRequest request) {
        String message = trafficControllerService.applyPriority(request);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
