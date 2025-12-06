package com.smartwater.backend.controller;

import com.smartwater.backend.dto.ResendVerificationRequest;
import com.smartwater.backend.dto.UserProfileResponse;
import com.smartwater.backend.dto.UserUpdateRequest;
import com.smartwater.backend.model.User;
import com.smartwater.backend.security.JwtUtil;
import com.smartwater.backend.service.EmailService;
import com.smartwater.backend.service.EmailVerificationService;
import com.smartwater.backend.service.PasswordResetService;
import com.smartwater.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetService passwordResetService;


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User userInput) {

        Map<String, Object> response = new HashMap<>();

        try {
            User existing = null;


            try {
                existing = userService.getUserByEmail(userInput.getEmail());
            } catch (RuntimeException ignored) {

            }

            if (existing != null) {

                if (existing.isEmailVerified()) {

                    response.put("message",
                            "Email already registered and verified. Please login.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                } else {

                    String newToken =
                            emailVerificationService.createVerificationToken(existing);
                    emailService.sendVerificationEmail(existing.getEmail(), newToken);

                    response.put("message",
                            "This email is already registered but not verified. " +
                                    "We have sent a new verification email.");
                    response.put("email", existing.getEmail());
                    return ResponseEntity.ok(response);
                }
            }


            User saved = userService.registerUser(userInput);

            String verifyToken = emailVerificationService.createVerificationToken(saved);
            emailService.sendVerificationEmail(saved.getEmail(), verifyToken);

            response.put("message",
                    "Registration successful. Please check your email to verify your account.");
            response.put("email", saved.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {

        String email = loginRequest.get("email");
        String password = loginRequest.get("password");


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        User user = userService.getUserByEmail(email);
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email is not verified. Please check your inbox to verify your account.");
        }


        String token = jwtUtil.generateToken(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", email);
        response.put("message", "Login successful!");

        return response;
    }


    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                System.out.println("Logout with invalid token: " + e.getMessage());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful! Please remove the token on client side.");
        if (email != null) {
            response.put("email", email);
        }

        return response;
    }


    @GetMapping("/me")
    public UserProfileResponse getCurrentUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        return new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getContact(),
                user.getCreatedAt()
        );
    }


    @PutMapping("/me")
    public UserProfileResponse updateCurrentUserProfile(@RequestBody UserUpdateRequest updateRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User updatedUser = userService.updateCurrentUser(email, updateRequest);

        return new UserProfileResponse(
                updatedUser.getId(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getEmail(),
                updatedUser.getContact(),
                updatedUser.getCreatedAt()
        );
    }


    @GetMapping("/verify-email")
    public Map<String, Object> verifyEmail(@RequestParam("token") String token) {

        emailVerificationService.confirmEmail(token);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Email verification successful. You can now log in.");
        return response;
    }


    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        Map<String, Object> response = new HashMap<>();

        response.put("message", "If this email is registered and verified, a password reset link has been sent.");

        passwordResetService.findUserByEmail(email).ifPresent(user -> {


            if (!user.isEmailVerified()) {
                return;
            }

            String resetToken = passwordResetService.createResetTokenForUser(user);
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        });

        return response;
    }


    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> request) {

        String token = request.get("token");
        String newPassword = request.get("newPassword");

        passwordResetService.resetPassword(token, newPassword);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password has been reset successfully.");
        return response;
    }


    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(
            @RequestBody ResendVerificationRequest request) {

        userService.resendVerificationEmail(request.getEmail());

        Map<String, String> body = new HashMap<>();
        body.put("message",
                "If this email is registered and not yet verified, a new verification link has been sent.");
        return ResponseEntity.ok(body);
    }
}
