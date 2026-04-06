package com.smartwater.backend.service;

import com.smartwater.backend.dto.AlertResponse;
import com.smartwater.backend.dto.WaterReadingRequest;
import com.smartwater.backend.model.Alert;
import com.smartwater.backend.model.User;
import com.smartwater.backend.repository.AlertRepository;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private static final double MIN_PH = 6.5;
    private static final double MAX_PH = 8.5;
    private static final double MAX_TURBIDITY = 5.0; // NTU

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    public AlertService(AlertRepository alertRepository, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
    }

    public AlertResponse evaluateReading(WaterReadingRequest reading) {
        if (reading.getPh() == null && reading.getTurbidity() == null) {
            throw new IllegalArgumentException("At least one reading (pH or turbidity) is required.");
        }

        boolean phOut = false;
        boolean turbOut = false;
        double phDelta = 0.0;
        double turbExcess = 0.0;

        StringBuilder message = new StringBuilder();

        if (reading.getPh() != null) {
            double ph = reading.getPh();
            if (ph < MIN_PH || ph > MAX_PH) {
                phOut = true;
                phDelta = (ph < MIN_PH) ? MIN_PH - ph : ph - MAX_PH;
                message.append("pH level is outside the safe range. ");
            }
        }

        if (reading.getTurbidity() != null) {
            double turbidity = reading.getTurbidity();
            if (turbidity > MAX_TURBIDITY) {
                turbOut = true;
                turbExcess = turbidity - MAX_TURBIDITY;
                message.append("Turbidity is too high. ");
            }
        }

        boolean alert = phOut || turbOut;
        String severity;
        String status;

        if (!alert) {
            severity = "SAFE";
            status = "SAFE";
        } else {
            int violations = 0;
            if (phOut) violations++;
            if (turbOut) violations++;

            if (violations == 1 && phDelta < 1.0 && turbExcess < 2.0) {
                severity = "LOW";
            } else if (violations == 1) {
                severity = "MEDIUM";
            } else {
                severity = "HIGH";
            }
            status = "POLLUTED";
        }

        return new AlertResponse(alert, status, severity, message.toString().trim());
    }

    /**
     * Evaluate and save alert to database
     */
    public AlertResponse evaluateAndSave(WaterReadingRequest reading, String email) {
        AlertResponse response = evaluateReading(reading);
        
        User user = userRepository.findByEmail(email).orElse(null);
        
        Alert alertEntity = new Alert();
        alertEntity.setUser(user);
        alertEntity.setAlert(response.isAlert());
        alertEntity.setSeverity(response.getSeverity());
        alertEntity.setStatus(response.getStatus());
        alertEntity.setMessage(response.getMessage());
        alertEntity.setPhValue(reading.getPh());
        alertEntity.setTemperature(reading.getTemperature());
        alertEntity.setTurbidity(reading.getTurbidity());
        alertEntity.setLocation(reading.getLocation());
        
        Alert saved = alertRepository.save(alertEntity);
        
        return toResponse(saved);
    }

    /**
     * Get alert history for user
     */
    public List<AlertResponse> getAlertsForUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return List.of();
        }
        return alertRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all alerts (admin)
     */
    public List<AlertResponse> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AlertResponse toResponse(Alert alert) {
        AlertResponse response = new AlertResponse(
                alert.isAlert(),
                alert.getStatus(),
                alert.getSeverity(),
                alert.getMessage()
        );
        response.setId(alert.getId());
        response.setPhValue(alert.getPhValue());
        response.setTemperature(alert.getTemperature());
        response.setTurbidity(alert.getTurbidity());
        response.setLocation(alert.getLocation());
        if (alert.getCreatedAt() != null) {
            response.setTimestamp(alert.getCreatedAt().toString());
        }
        return response;
    }
}
