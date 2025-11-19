package com.smartwater.backend.repository;

import com.smartwater.backend.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    // 某个用户的所有数据（按时间倒序）
    List<SensorData> findByUser_IdOrderByTimestampDesc(Long userId);

    // 某个用户最新一条记录
    Optional<SensorData> findFirstByUser_IdOrderByTimestampDesc(Long userId);

    // 某个用户在时间范围内的数据（按时间正序排）
    List<SensorData> findByUser_IdAndTimestampBetweenOrderByTimestampAsc(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );
}
