package com.smartwater.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SensorDataRequest {

    @NotNull(message = "pH value is required")
    @DecimalMin(value = "0.0", message = "pH cannot be less than 0.0")
    @DecimalMax(value = "14.0", message = "pH cannot be greater than 14.0")
    private Double ph;

    @NotNull(message = "Temperature is required")
    @DecimalMin(value = "-10.0", message = "Temperature is too low")
    @DecimalMax(value = "100.0", message = "Temperature is too high")
    private Double temperature;

    @NotNull(message = "Turbidity is required")
    @DecimalMin(value = "0.0", message = "Turbidity cannot be negative")
    private Double turbidity;

    @Size(max = 255, message = "Location cannot be longer than 255 characters")
    private String location;

    // ===== Getter / Setter =====

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(Double turbidity) {
        this.turbidity = turbidity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
