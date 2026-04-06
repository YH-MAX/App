package com.smartwater.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String contact;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;


    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;


    @Column(name = "role", nullable = false, length = 20)
    private String role;

    // Twitter-like profile fields
    @Column(name = "bio", length = 160)
    private String bio;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "header_image_url", length = 500)
    private String headerImageUrl;

    @Column(name = "follower_count")
    private Integer followerCount = 0;

    @Column(name = "following_count")
    private Integer followingCount = 0;

    @Column(name = "post_count")
    private Integer postCount = 0;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.role == null || this.role.isBlank()) {
            this.role = "USER";
        }
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public boolean isExpert() {
        if (role == null) return false;
        String r = role.toUpperCase();
        return "EXPERT".equals(r) || "ADMIN".equals(r);
    }

    // Twitter-like profile getters/setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getHeaderImageUrl() { return headerImageUrl; }
    public void setHeaderImageUrl(String headerImageUrl) { this.headerImageUrl = headerImageUrl; }

    public Integer getFollowerCount() { return followerCount != null ? followerCount : 0; }
    public void setFollowerCount(Integer followerCount) { this.followerCount = followerCount; }

    public Integer getFollowingCount() { return followingCount != null ? followingCount : 0; }
    public void setFollowingCount(Integer followingCount) { this.followingCount = followingCount; }

    public Integer getPostCount() { return postCount != null ? postCount : 0; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }
}
