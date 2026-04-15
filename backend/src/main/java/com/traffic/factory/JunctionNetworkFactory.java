package com.traffic.factory;

import com.traffic.model.Intersection;
import com.traffic.model.JunctionNetwork;

/**
 * Creational Factory pattern: creates the complete multi-junction layout in one
 * place instead of scattering construction logic through controllers/services.
 */
public final class JunctionNetworkFactory {

    private JunctionNetworkFactory() {}

    public static JunctionNetwork createSouthEndNetwork() {
        JunctionNetwork network = new JunctionNetwork();
        network.addIntersection(new Intersection("J1", "South End Main Circle"));
        network.addIntersection(new Intersection("J2", "Lalbagh Approach"));
        network.addIntersection(new Intersection("J3", "Jayanagar 9th Block"));
        network.addIntersection(new Intersection("J4", "Metro Feeder Road"));
        return network;
    }
}
