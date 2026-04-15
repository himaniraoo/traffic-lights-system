package com.traffic.command;

import com.traffic.model.PriorityRequest;
import com.traffic.service.TrafficControllerService;

public class PrioritySignalCommand implements TrafficCommand {
    private final TrafficControllerService trafficControllerService;
    private final PriorityRequest request;

    public PrioritySignalCommand(TrafficControllerService trafficControllerService, PriorityRequest request) {
        this.trafficControllerService = trafficControllerService;
        this.request = request;
    }

    @Override
    public String execute() {
        return trafficControllerService.applyPriority(request);
    }
}
