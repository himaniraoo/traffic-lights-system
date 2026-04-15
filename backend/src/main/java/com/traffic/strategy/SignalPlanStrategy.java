package com.traffic.strategy;

import com.traffic.model.Direction;
import com.traffic.model.Intersection;

public interface SignalPlanStrategy {
    void apply(Intersection intersection, Direction direction);
}
