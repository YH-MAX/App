package com.smartwater.backend.service;

import com.smartwater.backend.model.EmailVerificationToken;
import com.smartwater.backend.model.User;
import com.smartwater.backend.repository.EmailVerificationTokenRepository;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository,
                                    UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public String createVerificationToken(User user) {


        tokenRepository.deleteByUser(user);


        String tokenValue = UUID.randomUUID().toString();


        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        EmailVerificationToken token =
                new EmailVerificationToken(tokenValue, user, expiresAt);

        tokenRepository.save(token);

        return tokenValue;
    }


    @Transactional
    public void confirmEmail(String tokenValue) {

        EmailVerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (token.isUsed()) {
            throw new RuntimeException("Verification token has already been used.");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired.");
        }


        User user = token.getUser();
        user.setEmailVerified(true);
        user.setVerifiedAt(LocalDateTime.now());
        userRepository.save(user);


        token.setUsed(true);
        tokenRepository.save(token);
    }
}
