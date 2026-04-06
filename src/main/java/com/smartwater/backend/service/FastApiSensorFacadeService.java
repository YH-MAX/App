package com.smartwater.backend.service;

import com.smartwater.backend.dto.SensorDataResponse;
import com.smartwater.backend.dto.WaterIngestRequest;
import com.smartwater.backend.dto.WaterQualitySummaryResponse;
import com.smartwater.backend.exception.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FastApiSensorFacadeService {

    private final WebClient fastApiWebClient;

    public FastApiSensorFacadeService(WebClient fastApiWebClient) {
        this.fastApiWebClient = fastApiWebClient;
    }

    // ✅ Android BLE -> Spring /api/water/ingest -> FastAPI /api/ingest
    public SensorDataResponse ingestForUser(String email, WaterIngestRequest req) {
        return fastApiWebClient.post()
                .uri("/api/ingest")
                .header("X-User-Email", email) // ✅ 不需要 Android 传 email（避免硬编码/伪造）
                .bodyValue(req)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("FastAPI 4xx: " + msg))
                )
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("FastAPI 5xx: " + msg))
                )
                .bodyToMono(SensorDataResponse.class)
                .block();
    }

    public SensorDataResponse getLatest(String email) {
        return fastApiWebClient.get()
                .uri("/api/me/latest")
                .header("X-User-Email", email)
                .retrieve()
                .onStatus(status -> status.value() == 404, resp ->
                        resp.bodyToMono(String.class)
                                .map(msg -> new NotFoundException("No sensor data (FastAPI): " + msg))
                )
                .bodyToMono(SensorDataResponse.class)
                .block();
    }

    public List<SensorDataResponse> getRange(String email, LocalDateTime from, LocalDateTime to) {
        String fromStr = from.format(DateTimeFormatter.ISO_DATE_TIME);
        String toStr = to.format(DateTimeFormatter.ISO_DATE_TIME);

        return fastApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/me/range")
                        .queryParam("from", fromStr)
                        .queryParam("to", toStr)
                        .build())
                .header("X-User-Email", email)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SensorDataResponse>>() {})
                .block();
    }

    public WaterQualitySummaryResponse getSummary(String email, LocalDateTime from, LocalDateTime to) {
        String fromStr = from.format(DateTimeFormatter.ISO_DATE_TIME);
        String toStr = to.format(DateTimeFormatter.ISO_DATE_TIME);

        return fastApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/me/summary")
                        .queryParam("from", fromStr)
                        .queryParam("to", toStr)
                        .build())
                .header("X-User-Email", email)
                .retrieve()
                .bodyToMono(WaterQualitySummaryResponse.class)
                .block();
    }
}
