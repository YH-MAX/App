package com.smartwater.backend.repository;

import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.PostRetweet;
import com.smartwater.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PostRetweet entity - handles retweets and quote tweets
 */
@Repository
public interface PostRetweetRepository extends JpaRepository<PostRetweet, Long> {

    // Check if user retweeted a post (simple retweet only)
    boolean existsByUserAndOriginalPostAndIsQuoteTweetFalse(User user, CommunityPost originalPost);

    // Check if user retweeted a post (any type)
    boolean existsByUserAndOriginalPost(User user, CommunityPost originalPost);

    // Find specific retweet
    Optional<PostRetweet> findByUserAndOriginalPostAndIsQuoteTweetFalse(User user, CommunityPost originalPost);

    // Delete simple retweet
    void deleteByUserAndOriginalPostAndIsQuoteTweetFalse(User user, CommunityPost originalPost);

    // Get all retweets for a post
    List<PostRetweet> findByOriginalPost(CommunityPost originalPost);
    Page<PostRetweet> findByOriginalPost(CommunityPost originalPost, Pageable pageable);

    // Get all quote tweets for a post
    List<PostRetweet> findByOriginalPostAndIsQuoteTweetTrue(CommunityPost originalPost);
    Page<PostRetweet> findByOriginalPostAndIsQuoteTweetTrue(CommunityPost originalPost, Pageable pageable);

    // Get all retweets by a user
    List<PostRetweet> findByUserOrderByCreatedAtDesc(User user);
    Page<PostRetweet> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Count retweets for a post
    long countByOriginalPost(CommunityPost originalPost);

    // Count simple retweets for a post
    long countByOriginalPostAndIsQuoteTweetFalse(CommunityPost originalPost);

    // Count quote tweets for a post
    long countByOriginalPostAndIsQuoteTweetTrue(CommunityPost originalPost);

    // Get user IDs who retweeted a post
    @Query("SELECT pr.user.id FROM PostRetweet pr WHERE pr.originalPost = ?1")
    List<Long> findUserIdsWhoRetweeted(CommunityPost originalPost);

    // Check if user retweeted multiple posts (useful for feed)
    @Query("SELECT pr.originalPost.id FROM PostRetweet pr WHERE pr.user = ?1 AND pr.originalPost.id IN ?2 AND pr.isQuoteTweet = false")
    List<Long> findRetweetedPostIds(User user, List<Long> postIds);
}
