package com.smartwater.backend.dto;

import com.smartwater.backend.model.WaterQualityStatus;
import lombok.Data;


@Data
public class WaterQualitySummaryResponse {

    private long totalRecords;
    private double avgPh;
    private double avgTemperature;
    private double avgTurbidity;

    private long safeCount;
    private long moderateCount;
    private long pollutedCount;

    private WaterQualityStatus overallStatus;
}
