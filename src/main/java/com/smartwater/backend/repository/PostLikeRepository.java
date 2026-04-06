package com.smartwater.backend.repository;

import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.PostLike;
import com.smartwater.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PostLike entity - handles individual post likes
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Check if user liked a post
    boolean existsByUserAndPost(User user, CommunityPost post);

    // Find specific like
    Optional<PostLike> findByUserAndPost(User user, CommunityPost post);

    // Delete like
    void deleteByUserAndPost(User user, CommunityPost post);

    // Get all likes for a post
    List<PostLike> findByPost(CommunityPost post);
    Page<PostLike> findByPost(CommunityPost post, Pageable pageable);

    // Get all posts liked by a user
    List<PostLike> findByUserOrderByCreatedAtDesc(User user);
    Page<PostLike> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Count likes for a post
    long countByPost(CommunityPost post);

    // Get user IDs who liked a post
    @Query("SELECT pl.user.id FROM PostLike pl WHERE pl.post = ?1")
    List<Long> findUserIdsWhoLiked(CommunityPost post);

    // Check if user liked multiple posts (useful for feed)
    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user = ?1 AND pl.post.id IN ?2")
    List<Long> findLikedPostIds(User user, List<Long> postIds);
}
