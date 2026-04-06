package com.smartwater.backend.repository;

import com.smartwater.backend.model.User;
import com.smartwater.backend.model.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserFollow entity - handles follow relationships
 */
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    // Check if user A follows user B
    boolean existsByFollowerAndFollowing(User follower, User following);

    // Find follow relationship
    Optional<UserFollow> findByFollowerAndFollowing(User follower, User following);

    // Get all followers of a user (people who follow this user)
    List<UserFollow> findByFollowing(User following);
    Page<UserFollow> findByFollowing(User following, Pageable pageable);

    // Get all users that a user is following
    List<UserFollow> findByFollower(User follower);
    Page<UserFollow> findByFollower(User follower, Pageable pageable);

    // Count followers
    long countByFollowing(User following);

    // Count following
    long countByFollower(User follower);

    // Delete follow relationship
    void deleteByFollowerAndFollowing(User follower, User following);

    // Get follower IDs for a user
    @Query("SELECT uf.follower.id FROM UserFollow uf WHERE uf.following = ?1")
    List<Long> findFollowerIds(User following);

    // Get following IDs for a user
    @Query("SELECT uf.following.id FROM UserFollow uf WHERE uf.follower = ?1")
    List<Long> findFollowingIds(User follower);
}
