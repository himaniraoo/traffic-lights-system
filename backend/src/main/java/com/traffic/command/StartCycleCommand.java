package com.traffic.command;

import com.traffic.service.TrafficControllerService;

public class StartCycleCommand implements TrafficCommand {
    private final TrafficControllerService trafficControllerService;

    public StartCycleCommand(TrafficControllerService trafficControllerService) {
        this.trafficControllerService = trafficControllerService;
    }

    @Override
    public String execute() {
        return trafficControllerService.startCycle();
    }
}
