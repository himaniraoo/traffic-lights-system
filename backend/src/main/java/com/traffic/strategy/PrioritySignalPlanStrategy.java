package com.traffic.strategy;

import com.traffic.model.Direction;
import com.traffic.model.Intersection;
import com.traffic.model.SignalState;

public class PrioritySignalPlanStrategy implements SignalPlanStrategy {
    @Override
    public void apply(Intersection intersection, Direction direction) {
        intersection.activateOnly(direction, SignalState.GREEN);
    }
}
