package com.smartwater.backend.controller;

import com.smartwater.backend.dto.SensorDataRequest;
import com.smartwater.backend.dto.SensorDataResponse;
import com.smartwater.backend.dto.WaterQualitySummaryResponse;
import com.smartwater.backend.model.SensorData;
import com.smartwater.backend.service.SensorDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    private final SensorDataService sensorDataService;

    public SensorController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }


    @PostMapping("/upload")
    public SensorData uploadSensorData(@RequestBody @Valid SensorDataRequest req,
                                       @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        SensorData data = new SensorData();
        data.setPh(req.getPh());
        data.setTemperature(req.getTemperature());
        data.setTurbidity(req.getTurbidity());
        data.setLocation(req.getLocation());

        return sensorDataService.saveSensorDataForUser(data, email);
    }


    @GetMapping("/me/latest")
    public SensorDataResponse getMyLatest(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return sensorDataService.getLatestForUserWithStatus(email);
    }


    @GetMapping("/me/range")
    public List<SensorDataResponse> getMyDataInRange(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        String email = userDetails.getUsername();
        return sensorDataService.getDataForUserInRangeWithStatus(email, from, to);
    }


    @GetMapping("/me/summary")
    public WaterQualitySummaryResponse getMySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        String email = userDetails.getUsername();
        return sensorDataService.getSummaryForUserInRange(email, from, to);
    }
}
