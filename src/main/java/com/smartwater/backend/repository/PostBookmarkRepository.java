package com.smartwater.backend.repository;

import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.PostBookmark;
import com.smartwater.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PostBookmark entity - handles bookmarked posts
 */
@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

    // Check if user bookmarked a post
    boolean existsByUserAndPost(User user, CommunityPost post);

    // Find specific bookmark
    Optional<PostBookmark> findByUserAndPost(User user, CommunityPost post);

    // Delete bookmark
    void deleteByUserAndPost(User user, CommunityPost post);

    // Get all bookmarks for a user (sorted by newest first)
    List<PostBookmark> findByUserOrderByCreatedAtDesc(User user);
    Page<PostBookmark> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Count bookmarks for a post
    long countByPost(CommunityPost post);

    // Count user's total bookmarks
    long countByUser(User user);

    // Check if user bookmarked multiple posts (useful for feed)
    @Query("SELECT pb.post.id FROM PostBookmark pb WHERE pb.user = ?1 AND pb.post.id IN ?2")
    List<Long> findBookmarkedPostIds(User user, List<Long> postIds);
}
