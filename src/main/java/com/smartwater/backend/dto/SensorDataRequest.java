package com.smartwater.backend.dto;

public class SensorDataRequest {

    private double ph;
    private double temperature;
    private double turbidity;
    private String location;

    // ======= getters & setters =======

    public double getPh() {
        return ph;
    }

    public void setPh(double ph) {
        this.ph = ph;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(double turbidity) {
        this.turbidity = turbidity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
