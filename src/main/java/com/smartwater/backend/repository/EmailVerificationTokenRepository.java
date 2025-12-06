package com.smartwater.backend.repository;

import com.smartwater.backend.model.EmailVerificationToken;
import com.smartwater.backend.model.User;   // ⬅ 这一行要有！
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUser(User user);
}
