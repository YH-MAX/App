package com.smartwater.backend.integration;

import com.smartwater.backend.dto.SensorDataRequest;
import com.smartwater.backend.dto.SensorDataResponse;
import com.smartwater.backend.dto.WaterQualitySummaryResponse;
import com.smartwater.backend.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FastApiSensorClient {

    private final WebClient fastApiWebClient;

    public FastApiSensorClient(WebClient fastApiWebClient) {
        this.fastApiWebClient = fastApiWebClient;
    }

    // Android BLE -> Spring -> FastAPI
    public SensorDataResponse ingest(String email, SensorDataRequest req) {
        FastApiIngestRequest body = new FastApiIngestRequest(email,
                req.getPh(), req.getTemperature(), req.getTurbidity(), req.getLocation());

        try {
            return fastApiWebClient.post()
                    .uri("/api/ingest")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(SensorDataResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapFastApiError(e);
        }
    }

    // Dashboard latest
    public SensorDataResponse getLatest(String email) {
        try {
            return fastApiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/me/latest")
                            .queryParam("email", email)
                            .build())
                    .retrieve()
                    .bodyToMono(SensorDataResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapFastApiError(e);
        }
    }

    // Device latest (for Dashboard "SmartWater Monitor")
    public SensorDataResponse getDeviceLatest(String deviceId) {
        try {
            return fastApiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/latest")
                            .queryParam("device_id", deviceId)
                            .build())
                    .retrieve()
                    .bodyToMono(SensorDataResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapFastApiError(e);
        }
    }

    public List<SensorDataResponse> getRange(String email, LocalDateTime from, LocalDateTime to) {
        System.out.println(" [FastApiSensorClient] getRange() using WebClient for: " + email);

        try {
            return fastApiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/me/range")
                            .queryParam("email", email)
                            .queryParam("from", from.toString())
                            .queryParam("to", to.toString())
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SensorDataResponse>>() {})
                    .block();

        } catch (WebClientResponseException e) {
            System.err.println(" [FastApiSensorClient] WebClient error: " + e.getMessage());
            throw mapFastApiError(e);
        } catch (Exception e) {
            System.err.println(" [FastApiSensorClient] General error: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public WaterQualitySummaryResponse getSummary(String email, LocalDateTime from, LocalDateTime to) {
        try {
            return fastApiWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/me/summary")
                            .queryParam("email", email)
                            .queryParam("from", from.toString())
                            .queryParam("to", to.toString())
                            .build())
                    .retrieve()
                    .bodyToMono(WaterQualitySummaryResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw mapFastApiError(e);
        }
    }

    private RuntimeException mapFastApiError(WebClientResponseException e) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new NotFoundException("FastAPI: data not found");
        }
        return new RuntimeException("FastAPI error: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
    }

    // ✅ 内部 DTO：带 user identity
    public static class FastApiIngestRequest {
        public String email;
        public double ph;
        public double temperature;
        public double turbidity;
        public String location;

        public FastApiIngestRequest(String email, double ph, double temperature, double turbidity, String location) {
            this.email = email;
            this.ph = ph;
            this.temperature = temperature;
            this.turbidity = turbidity;
            this.location = location;
        }
    }
}
