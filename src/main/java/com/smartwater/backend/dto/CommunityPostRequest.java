package com.smartwater.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CommunityPostRequest {


    @Size(max = 1000, message = "Content must be at most 1000 characters")
    private String content;


    @Size(max = 500, message = "Photo URL is too long")
    @Pattern(
            regexp = "^(https?://).*$",
            message = "Photo URL must start with http:// or https://"
    )
    private String photoUrl;


    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;


    @DecimalMin(value = "0.0", message = "pH must be >= 0")
    @DecimalMax(value = "14.0", message = "pH must be <= 14")
    private Double ph;


    @DecimalMin(value = "0.0", message = "Temperature must be >= 0°C")
    @DecimalMax(value = "60.0", message = "Temperature must be <= 60°C")
    private Double temperature;


    @DecimalMin(value = "0.0", message = "Turbidity must be >= 0")
    private Double turbidity;


    @Pattern(
            regexp = "INFO|ALERT|QUESTION",
            message = "Type must be INFO, ALERT or QUESTION"
    )
    private String type;

    // ===== Getter / Setter =====

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
