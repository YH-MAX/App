package com.smartwater.backend.controller;

import com.smartwater.backend.dto.AlertResponse;
import com.smartwater.backend.dto.WaterReadingRequest;
import com.smartwater.backend.service.AlertService;
import com.smartwater.backend.service.PollutionReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;
    private final PollutionReportService pollutionReportService;

    public AlertController(AlertService alertService,
                           PollutionReportService pollutionReportService) {
        this.alertService = alertService;
        this.pollutionReportService = pollutionReportService;
    }

    /**
     * Evaluate water reading and check for alerts
     * POST /api/alerts/evaluate
     */
    @PostMapping("/evaluate")
    public ResponseEntity<AlertResponse> evaluate(
            @AuthenticationPrincipal UserDetails currentUser,
            @Valid @RequestBody WaterReadingRequest readingRequest
    ) {
        String email = currentUser != null ? currentUser.getUsername() : null;
        
        AlertResponse result;
        if (email != null) {
            // Save to history if user is authenticated
            result = alertService.evaluateAndSave(readingRequest, email);
        } else {
            result = alertService.evaluateReading(readingRequest);
        }

        boolean severeAlert = result.isAlert() && "HIGH".equals(result.getSeverity());

        if (severeAlert && email != null) {
            StringBuilder desc = new StringBuilder("Auto-generated HIGH severity alert. ");
            if (result.getMessage() != null && !result.getMessage().isBlank()) {
                desc.append(result.getMessage()).append(" ");
            }

            desc.append("Readings: ");
            if (readingRequest.getPh() != null) {
                desc.append("pH=").append(readingRequest.getPh()).append(" ");
            }
            if (readingRequest.getTemperature() != null) {
                desc.append("Temp=").append(readingRequest.getTemperature()).append("°C ");
            }
            if (readingRequest.getTurbidity() != null) {
                desc.append("Turbidity=").append(readingRequest.getTurbidity()).append(" NTU ");
            }

            pollutionReportService.createReport(
                    email,
                    desc.toString().trim(),
                    null,
                    null,
                    true
            );
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get alert history for current user
     * GET /api/alerts/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<AlertResponse>> getMyAlerts(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        String email = currentUser.getUsername();
        List<AlertResponse> alerts = alertService.getAlertsForUser(email);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get all alerts (admin/public view)
     * GET /api/alerts
     */
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        List<AlertResponse> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }
}
