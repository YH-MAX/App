package com.smartwater.backend.controller;

import com.smartwater.backend.dto.PageResponse;
import com.smartwater.backend.dto.UserProfileResponse;
import com.smartwater.backend.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for follow/unfollow operations (Twitter-like)
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * Follow a user
     * POST /api/users/{userId}/follow
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        followService.followUser(currentUser.getUsername(), userId);
        return ResponseEntity.ok(Map.of("message", "Successfully followed user", "following", true));
    }

    /**
     * Unfollow a user
     * DELETE /api/users/{userId}/follow
     */
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        followService.unfollowUser(currentUser.getUsername(), userId);
        return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user", "following", false));
    }

    /**
     * Check if current user follows target user
     * GET /api/users/{userId}/is-following
     */
    @GetMapping("/{userId}/is-following")
    public ResponseEntity<?> isFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        boolean isFollowing = followService.isFollowing(currentUser.getUsername(), userId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    /**
     * Get followers of a user
     * GET /api/users/{userId}/followers
     */
    @GetMapping("/{userId}/followers")
    public PageResponse<UserProfileResponse> getFollowers(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        String currentEmail = currentUser != null ? currentUser.getUsername() : null;
        return followService.getFollowers(userId, currentEmail, page, size);
    }

    /**
     * Get users that a user is following
     * GET /api/users/{userId}/following
     */
    @GetMapping("/{userId}/following")
    public PageResponse<UserProfileResponse> getFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        String currentEmail = currentUser != null ? currentUser.getUsername() : null;
        return followService.getFollowing(userId, currentEmail, page, size);
    }

    /**
     * Get user profile by ID
     * GET /api/users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    public UserProfileResponse getUserProfile(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String currentEmail = currentUser != null ? currentUser.getUsername() : null;
        return followService.getUserProfile(userId, currentEmail);
    }

    /**
     * Get current user's profile
     * GET /api/users/me/profile
     */
    @GetMapping("/me/profile")
    public UserProfileResponse getMyProfile(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return followService.getUserProfileByEmail(currentUser.getUsername(), currentUser.getUsername());
    }
}
