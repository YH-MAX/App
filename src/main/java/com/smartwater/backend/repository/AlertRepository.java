package com.smartwater.backend.repository;

import com.smartwater.backend.model.Alert;
import com.smartwater.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByUserOrderByCreatedAtDesc(User user);
    
    List<Alert> findAllByOrderByCreatedAtDesc();
}
