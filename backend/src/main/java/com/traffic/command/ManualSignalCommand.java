package com.traffic.command;

import com.traffic.model.ManualSignalRequest;
import com.traffic.service.TrafficControllerService;

public class ManualSignalCommand implements TrafficCommand {
    private final TrafficControllerService trafficControllerService;
    private final ManualSignalRequest request;

    public ManualSignalCommand(TrafficControllerService trafficControllerService, ManualSignalRequest request) {
        this.trafficControllerService = trafficControllerService;
        this.request = request;
    }

    @Override
    public String execute() {
        return trafficControllerService.applyManualSignal(request);
    }
}
