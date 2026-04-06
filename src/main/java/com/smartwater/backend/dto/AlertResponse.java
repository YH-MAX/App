package com.smartwater.backend.dto;

public class AlertResponse {
    private Long id;
    private boolean alert;
    private String status;   // SAFE / POLLUTED
    private String severity; // SAFE / LOW / MEDIUM / HIGH
    private String message;
    private String timestamp;
    private Double phValue;
    private Double temperature;
    private Double turbidity;
    private String location;

    public AlertResponse() {}

    public AlertResponse(boolean alert, String status, String severity, String message) {
        this.alert = alert;
        this.status = status;
        this.severity = severity;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isAlert() { return alert; }
    public void setAlert(boolean alert) { this.alert = alert; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Double getPhValue() { return phValue; }
    public void setPhValue(Double phValue) { this.phValue = phValue; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getTurbidity() { return turbidity; }
    public void setTurbidity(Double turbidity) { this.turbidity = turbidity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
