package com.smartwater.backend.repository;

import com.smartwater.backend.model.PollutionReport;
import com.smartwater.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollutionReportRepository extends JpaRepository<PollutionReport, Long> {


    List<PollutionReport> findByUserOrderByCreatedAtDesc(User user);
}
