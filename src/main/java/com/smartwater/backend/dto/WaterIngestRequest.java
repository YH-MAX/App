package com.smartwater.backend.dto;

import jakarta.validation.constraints.NotNull;

public class WaterIngestRequest {
    @NotNull
    private Double ph;
    @NotNull
    private Double temperature;
    @NotNull
    private Double turbidity;

    private String location; // optional

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getTurbidity() { return turbidity; }
    public void setTurbidity(Double turbidity) { this.turbidity = turbidity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
