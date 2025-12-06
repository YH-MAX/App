package com.smartwater.backend.controller;

import com.smartwater.backend.dto.AlertResponse;
import com.smartwater.backend.dto.WaterReadingRequest;
import com.smartwater.backend.service.AlertService;
import com.smartwater.backend.service.PollutionReportService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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

    @PostMapping("/evaluate")
    public ResponseEntity<AlertResponse> evaluate(
            @AuthenticationPrincipal UserDetails currentUser,
            @Valid @RequestBody WaterReadingRequest readingRequest
    ) {
        AlertResponse result = alertService.evaluateReading(readingRequest);

        boolean severeAlert = result.isAlert() && "HIGH".equals(result.getSeverity());

        if (severeAlert && currentUser != null) {
            String email = currentUser.getUsername();

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
}
