package com.traffic.model;

public class TrafficDensityRequest {
    private String junctionId;
    private int vehicleCount;

    public String getJunctionId() { return junctionId; }
    public int getVehicleCount() { return vehicleCount; }

    public void setJunctionId(String junctionId) { this.junctionId = junctionId; }
    public void setVehicleCount(int vehicleCount) { this.vehicleCount = vehicleCount; }
}
