package com.smartwater.backend.service;

import com.smartwater.backend.dto.SensorDataResponse;
import com.smartwater.backend.dto.WaterQualitySummaryResponse;
import com.smartwater.backend.exception.NotFoundException;
import com.smartwater.backend.model.SensorData;
import com.smartwater.backend.model.User;
import com.smartwater.backend.model.WaterQualityStatus;
import com.smartwater.backend.repository.SensorDataRepository;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;
    private final UserRepository userRepository;

    public SensorDataService(SensorDataRepository sensorDataRepository,
                             UserRepository userRepository) {
        this.sensorDataRepository = sensorDataRepository;
        this.userRepository = userRepository;
    }


    public SensorData saveSensorDataForUser(SensorData data, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));

        data.setUser(user);

        return sensorDataRepository.save(data);
    }


    private WaterQualityStatus classify(SensorData data) {
        int score = 0;

        double ph = data.getPh();
        if (ph < 6.5 || ph > 8.5) {
            score++;
        }

        double turbidity = data.getTurbidity();
        if (turbidity > 5.0) { // >5 NTU 视为浑浊
            score++;
        }

        double temp = data.getTemperature();
        if (temp < 5 || temp > 35) { // 温度太低/太高也记问题
            score++;
        }

        if (score == 0) {
            return WaterQualityStatus.SAFE;
        } else if (score == 1) {
            return WaterQualityStatus.MODERATE;
        } else {
            return WaterQualityStatus.POLLUTED;
        }
    }


    public SensorDataResponse toResponse(SensorData data) {
        if (data == null) {
            return null;
        }
        SensorDataResponse dto = new SensorDataResponse();
        dto.setId(data.getId());
        dto.setPh(data.getPh());
        dto.setTemperature(data.getTemperature());
        dto.setTurbidity(data.getTurbidity());
        dto.setLocation(data.getLocation());
        dto.setTimestamp(data.getTimestamp());
        dto.setStatus(classify(data));
        return dto;
    }


    public SensorDataResponse getLatestForUserWithStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));

        return sensorDataRepository
                .findFirstByUser_IdOrderByTimestampDesc(user.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("No sensor data found for user: " + email));
    }


    public List<SensorDataResponse> getDataForUserInRangeWithStatus(String email,
                                                                    LocalDateTime from,
                                                                    LocalDateTime to) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));

        List<SensorData> list = sensorDataRepository
                .findByUser_IdAndTimestampBetweenOrderByTimestampAsc(
                        user.getId(), from, to
                );

        return list.stream()
                .map(this::toResponse)
                .toList();
    }


    public WaterQualitySummaryResponse getSummaryForUserInRange(String email,
                                                                LocalDateTime from,
                                                                LocalDateTime to) {

        List<SensorDataResponse> list = getDataForUserInRangeWithStatus(email, from, to);

        WaterQualitySummaryResponse summary = new WaterQualitySummaryResponse();
        long total = list.size();
        summary.setTotalRecords(total);

        if (total == 0) {
            // 没有记录时，平均值 & 计数都设 0，overallStatus 交给前端看着处理（null）
            summary.setAvgPh(0.0);
            summary.setAvgTemperature(0.0);
            summary.setAvgTurbidity(0.0);
            summary.setSafeCount(0);
            summary.setModerateCount(0);
            summary.setPollutedCount(0);
            summary.setOverallStatus(null);
            return summary;
        }

        summary.setAvgPh(list.stream()
                .mapToDouble(SensorDataResponse::getPh)
                .average()
                .orElse(0.0));

        summary.setAvgTemperature(list.stream()
                .mapToDouble(SensorDataResponse::getTemperature)
                .average()
                .orElse(0.0));

        summary.setAvgTurbidity(list.stream()
                .mapToDouble(SensorDataResponse::getTurbidity)
                .average()
                .orElse(0.0));

        long safe = list.stream()
                .filter(d -> d.getStatus() == WaterQualityStatus.SAFE)
                .count();
        long moderate = list.stream()
                .filter(d -> d.getStatus() == WaterQualityStatus.MODERATE)
                .count();
        long polluted = list.stream()
                .filter(d -> d.getStatus() == WaterQualityStatus.POLLUTED)
                .count();

        summary.setSafeCount(safe);
        summary.setModerateCount(moderate);
        summary.setPollutedCount(polluted);

        if (polluted > 0) {
            summary.setOverallStatus(WaterQualityStatus.POLLUTED);
        } else if (moderate > 0) {
            summary.setOverallStatus(WaterQualityStatus.MODERATE);
        } else {
            summary.setOverallStatus(WaterQualityStatus.SAFE);
        }

        return summary;
    }
}
