package com.smartwater.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WaterProxyService {

    private final String fastApiBaseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public WaterProxyService(@Value("${fastapi.base-url}") String fastApiBaseUrl) {
        this.fastApiBaseUrl = fastApiBaseUrl;
        System.out.println("[WaterProxyService] FastAPI Base URL = " + fastApiBaseUrl);
    }

    public String getLatest() {
        String url = fastApiBaseUrl + "/api/latest";
        return restTemplate.getForObject(url, String.class);
    }

    public String getHistory(String range, int value) {
        String url = fastApiBaseUrl + "/api/history?range=" + range + "&value=" + value;
        return restTemplate.getForObject(url, String.class);
    }
}
