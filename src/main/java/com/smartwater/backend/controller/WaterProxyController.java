package com.smartwater.backend.controller;

import com.smartwater.backend.service.WaterProxyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/water")
public class WaterProxyController {

    private final WaterProxyService service;

    public WaterProxyController(WaterProxyService service) {
        this.service = service;
    }

    @GetMapping("/latest")
    public String latest() {
        return service.getLatest();
    }

    @GetMapping("/history")
    public String history(@RequestParam String range,
                          @RequestParam int value) {
        return service.getHistory(range, value);
    }
    
    // ✅ TEST: Verify Spring → FastAPI connection works
    @GetMapping("/test-connection")
    public String testFastApiConnection() {
        try {
            String url = "http://localhost:8001/api/me/range?email=aicrafter1412@gmail.com&from=2026-01-05T00:00:00&to=2026-01-07T00:00:00";
            System.out.println("📊 [TEST] Calling: " + url);
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("📊 [TEST] Got " + (response != null ? response.length() : 0) + " chars");
            return response != null ? response : "null response";
        } catch (Exception e) {
            System.err.println("❌ [TEST] Error: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
}
