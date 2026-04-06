package com.smartwater.backend.controller;

import com.smartwater.backend.dto.SensorDataRequest;
import com.smartwater.backend.dto.SensorDataResponse;
import com.smartwater.backend.dto.WaterQualitySummaryResponse;
import com.smartwater.backend.integration.FastApiSensorClient;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*")
public class SensorController {

    private final FastApiSensorClient fastApiSensorClient;

    public SensorController(FastApiSensorClient fastApiSensorClient) {
        this.fastApiSensorClient = fastApiSensorClient;
    }
    
    // ✅ TEST: Direct FastAPI call to debug connection
    @GetMapping("/test-fastapi")
    public String testFastApiConnection() {
        try {
            String url = "http://localhost:8001/api/me/range?email=aicrafter1412@gmail.com&from=2026-01-05T00:00:00&to=2026-01-07T00:00:00";
            System.out.println("📊 [TEST] Calling: " + url);
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("📊 [TEST] Response length: " + (response != null ? response.length() : "null"));
            return "FastAPI Response: " + (response != null ? response.substring(0, Math.min(500, response.length())) : "null");
        } catch (Exception e) {
            System.err.println("❌ [TEST] Error: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    // ✅ 兼容旧路径：/api/sensor/upload 也改成转发 FastAPI（不落 MySQL）
    @PostMapping("/upload")
    public SensorDataResponse uploadSensorData(
            @RequestBody @Valid SensorDataRequest req,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        return fastApiSensorClient.ingest(email, req);
    }

    // ✅ Dashboard: Get latest data for specific device (e.g., WATER_001)
    @GetMapping("/device/{deviceId}/latest")
    public SensorDataResponse getDeviceLatest(@PathVariable String deviceId) {
        System.out.println("📊 [SensorController] getDeviceLatest called for: " + deviceId);
        return fastApiSensorClient.getDeviceLatest(deviceId);
    }

    @GetMapping("/me/latest")
    public SensorDataResponse getMyLatest(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return fastApiSensorClient.getLatest(email);
    }

    @GetMapping("/me/range")
    public List<SensorDataResponse> getMyDataInRange(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        String email = userDetails.getUsername();
        System.out.println("📊 [SensorController] getMyDataInRange called for: " + email);
        
        try {
            List<SensorDataResponse> result = fastApiSensorClient.getRange(email, from, to);
            System.out.println("📊 [SensorController] Got " + (result != null ? result.size() : "null") + " records");
            return result != null ? result : java.util.Collections.emptyList();
        } catch (Exception e) {
            System.err.println("❌ [SensorController] Error: " + e.getMessage());
            // Return empty list instead of throwing to avoid 500 error
            return java.util.Collections.emptyList();
        }
    }

    @GetMapping("/me/summary")
    public WaterQualitySummaryResponse getMySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        String email = userDetails.getUsername();
        return fastApiSensorClient.getSummary(email, from, to);
    }
}
