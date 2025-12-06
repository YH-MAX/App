package com.smartwater.backend.repository;

import com.smartwater.backend.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {


    List<SensorData> findByUser_IdOrderByTimestampDesc(Long userId);


    Optional<SensorData> findFirstByUser_IdOrderByTimestampDesc(Long userId);


    List<SensorData> findByUser_IdAndTimestampBetweenOrderByTimestampAsc(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );
}
