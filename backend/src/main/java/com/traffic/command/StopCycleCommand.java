package com.traffic.command;

import com.traffic.service.TrafficControllerService;

public class StopCycleCommand implements TrafficCommand {
    private final TrafficControllerService trafficControllerService;

    public StopCycleCommand(TrafficControllerService trafficControllerService) {
        this.trafficControllerService = trafficControllerService;
    }

    @Override
    public String execute() {
        return trafficControllerService.stopCycle();
    }
}
