package com.smartwater.backend.controller;

import com.smartwater.backend.dto.WaterReadingRequest;
import com.smartwater.backend.service.AlertService;
import com.smartwater.backend.service.PollutionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private PollutionReportService pollutionReportService;


    @PostMapping("/evaluate")
    public Map<String, Object> evaluate(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody WaterReadingRequest readingRequest
    ) {

        Map<String, Object> result = alertService.evaluateReading(readingRequest);


        Object alertObj = result.get("alert");
        Object severityObj = result.get("severity");

        boolean severeAlert = false;
        if (alertObj instanceof Boolean && (Boolean) alertObj) {
            if (severityObj instanceof String && "HIGH".equals(severityObj)) {
                severeAlert = true;
            }
        }


        if (severeAlert && currentUser != null) {
            String email = currentUser.getUsername();

            StringBuilder desc = new StringBuilder("Auto-generated HIGH severity alert. ");
            Object msgObj = result.get("message");
            if (msgObj instanceof String && !((String) msgObj).isBlank()) {
                desc.append(msgObj).append(" ");
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


        return result;
    }
}
