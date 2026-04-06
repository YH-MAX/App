package com.smartwater.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorDataResponse {
    public Long id;
    public Double ph;
    public Double temperature;
    public Double turbidity;
    public String location;
    public String timestamp;
    public String email;
    public String status;

    // Default constructor needed for Jackson
    public SensorDataResponse() {}
}
