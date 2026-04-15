package com.traffic.controller;

import com.traffic.model.DiagnosticsResult;
import com.traffic.service.SystemManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final SystemManagerService systemManagerService;

    public MaintenanceController(SystemManagerService systemManagerService) {
        this.systemManagerService = systemManagerService;
    }

    @GetMapping("/status")
    public ResponseEntity<SystemManagerService.SystemStatus> getSystemStatus() {
        return ResponseEntity.ok(systemManagerService.getSystemStatus());
    }

    @PostMapping("/diagnostics")
    public ResponseEntity<DiagnosticsResult> runDiagnostics() {
        DiagnosticsResult result = systemManagerService.runDiagnostics();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetSystem() {
        String message = systemManagerService.resetToDefaults();
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/manager-state")
    public ResponseEntity<Map<String, String>> getManagerState() {
        return ResponseEntity.ok(Map.of("managerState", systemManagerService.getManagerState().name()));
    }
}
