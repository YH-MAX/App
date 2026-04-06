package com.smartwater.backend.service;

import com.smartwater.backend.dto.PageResponse;
import com.smartwater.backend.dto.UserProfileResponse;
import com.smartwater.backend.model.User;
import com.smartwater.backend.model.UserFollow;
import com.smartwater.backend.repository.UserFollowRepository;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing follow relationships between users
 */
@Service
public class FollowService {

    @Autowired
    private UserFollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Follow a user
     */
    @Transactional
    public void followUser(String currentUserEmail, Long targetUserId) {
        User follower = findUserByEmail(currentUserEmail);
        User following = findUserById(targetUserId);

        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("You already follow this user");
        }

        UserFollow follow = new UserFollow(follower, following);
        followRepository.save(follow);

        // Update cached counts
        following.setFollowerCount(following.getFollowerCount() + 1);
        follower.setFollowingCount(follower.getFollowingCount() + 1);
        userRepository.save(following);
        userRepository.save(follower);
    }

    /**
     * Unfollow a user
     */
    @Transactional
    public void unfollowUser(String currentUserEmail, Long targetUserId) {
        User follower = findUserByEmail(currentUserEmail);
        User following = findUserById(targetUserId);

        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("You are not following this user");
        }

        followRepository.deleteByFollowerAndFollowing(follower, following);

        // Update cached counts
        following.setFollowerCount(Math.max(0, following.getFollowerCount() - 1));
        follower.setFollowingCount(Math.max(0, follower.getFollowingCount() - 1));
        userRepository.save(following);
        userRepository.save(follower);
    }

    /**
     * Check if current user follows target user
     */
    public boolean isFollowing(String currentUserEmail, Long targetUserId) {
        User follower = findUserByEmail(currentUserEmail);
        User following = findUserById(targetUserId);
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    /**
     * Get followers of a user
     */
    public PageResponse<UserProfileResponse> getFollowers(Long userId, String currentUserEmail, Integer page, Integer size) {
        User user = findUserById(userId);
        User currentUser = currentUserEmail != null ? findUserByEmail(currentUserEmail) : null;

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 20 : size;
        Pageable pageable = PageRequest.of(p, s);

        Page<UserFollow> pageResult = followRepository.findByFollowing(user, pageable);

        PageResponse<UserProfileResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(uf -> toUserProfileResponse(uf.getFollower(), currentUser))
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        return resp;
    }

    /**
     * Get users that a user is following
     */
    public PageResponse<UserProfileResponse> getFollowing(Long userId, String currentUserEmail, Integer page, Integer size) {
        User user = findUserById(userId);
        User currentUser = currentUserEmail != null ? findUserByEmail(currentUserEmail) : null;

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 20 : size;
        Pageable pageable = PageRequest.of(p, s);

        Page<UserFollow> pageResult = followRepository.findByFollower(user, pageable);

        PageResponse<UserProfileResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(uf -> toUserProfileResponse(uf.getFollowing(), currentUser))
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        return resp;
    }

    /**
     * Get user profile with follow status
     */
    public UserProfileResponse getUserProfile(Long userId, String currentUserEmail) {
        User user = findUserById(userId);
        User currentUser = currentUserEmail != null ? findUserByEmail(currentUserEmail) : null;
        return toUserProfileResponse(user, currentUser);
    }

    /**
     * Get user profile by email
     */
    public UserProfileResponse getUserProfileByEmail(String email, String currentUserEmail) {
        User user = findUserByEmail(email);
        User currentUser = currentUserEmail != null ? findUserByEmail(currentUserEmail) : null;
        return toUserProfileResponse(user, currentUser);
    }

    // Helper methods
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private UserProfileResponse toUserProfileResponse(User user, User currentUser) {
        UserProfileResponse dto = new UserProfileResponse();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setContact(user.getContact());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setBio(user.getBio());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setHeaderImageUrl(user.getHeaderImageUrl());
        dto.setFollowerCount(user.getFollowerCount());
        dto.setFollowingCount(user.getFollowingCount());
        dto.setPostCount(user.getPostCount());
        dto.setRole(user.getRole());
        dto.setIsExpert(user.isExpert());

        // Check if current user follows this user
        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            dto.setIsFollowing(followRepository.existsByFollowerAndFollowing(currentUser, user));
        } else {
            dto.setIsFollowing(false);
        }

        return dto;
    }
}
