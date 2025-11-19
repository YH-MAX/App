package com.smartwater.backend.dto;

import com.smartwater.backend.model.WaterQualityStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class SensorDataResponse {

    private Long id;
    private double ph;
    private double temperature;
    private double turbidity;
    private String location;
    private LocalDateTime timestamp;


    private WaterQualityStatus status;
}
