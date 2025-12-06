package com.smartwater.backend.dto;

public class AlertResponse {
    private boolean alert;
    private String status;   // SAFE / POLLUTED
    private String severity; // SAFE / LOW / MEDIUM / HIGH
    private String message;

    public AlertResponse() {}

    public AlertResponse(boolean alert, String status, String severity, String message) {
        this.alert = alert;
        this.status = status;
        this.severity = severity;
        this.message = message;
    }

    public boolean isAlert() { return alert; }
    public void setAlert(boolean alert) { this.alert = alert; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
