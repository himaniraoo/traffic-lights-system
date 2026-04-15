package com.traffic.factory;

import com.traffic.model.Intersection;
import com.traffic.model.JunctionNetwork;
import com.traffic.model.JunctionType;
import com.traffic.model.TrafficLoad;

/**
 * Creational Factory pattern: creates the complete multi-junction layout in one
 * place instead of scattering construction logic through controllers/services.
 */
public final class JunctionNetworkFactory {

    private JunctionNetworkFactory() {}

    public static JunctionNetwork createSouthEndNetwork() {
        JunctionNetwork network = new JunctionNetwork();
        network.addIntersection(new Intersection("J1", "South End Main Circle", JunctionType.ROUNDABOUT_APPROACH, TrafficLoad.CRITICAL, "Major arterial circle"));
        network.addIntersection(new Intersection("J2", "Lalbagh Approach", JunctionType.FOUR_WAY, TrafficLoad.HIGH, "Garden access road"));
        network.addIntersection(new Intersection("J3", "Jayanagar 9th Block", JunctionType.FOUR_WAY, TrafficLoad.HIGH, "Commercial cross traffic"));
        network.addIntersection(new Intersection("J4", "Metro Feeder Road", JunctionType.BUS_CORRIDOR, TrafficLoad.MEDIUM, "Public transport feeder"));
        network.addIntersection(new Intersection("J5", "School Zone Crossing", JunctionType.SCHOOL_ZONE, TrafficLoad.MEDIUM, "Morning school dispersal"));
        network.addIntersection(new Intersection("J6", "Market T-Junction", JunctionType.T_JUNCTION, TrafficLoad.LOW, "Local market entry"));
        network.addIntersection(new Intersection("J7", "Hospital Priority Gate", JunctionType.PEDESTRIAN_CROSSING, TrafficLoad.HIGH, "Emergency vehicle access"));
        return network;
    }
}
