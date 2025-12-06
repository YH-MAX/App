package com.smartwater.backend.service;

import com.smartwater.backend.dto.UserUpdateRequest;
import com.smartwater.backend.model.User;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public User registerUser(User user) {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));


        return userRepository.save(user);
    }


    public User loginUser(String email, String password) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            } else {
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("User not found!");
        }
    }


    public User updateUser(User user) {
        return userRepository.save(user);
    }


    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }


    public User updateCurrentUser(String email, UserUpdateRequest updateRequest) {

        User user = getUserByEmail(email);

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }

        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }

        if (updateRequest.getContact() != null) {
            user.setContact(updateRequest.getContact());
        }

        return userRepository.save(user);
    }


    public void resendVerificationEmail(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {

            return;
        }

        User user = optionalUser.get();


        if (user.isEmailVerified()) {
            return;
        }


        String newToken = emailVerificationService.createVerificationToken(user);


        emailService.sendVerificationEmail(user.getEmail(), newToken);
    }
}
