package com.smartwater.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for retweets and quote tweets
 * Like Twitter's retweet and quote tweet functionality
 */
@Entity
@Table(name = "post_retweets")
public class PostRetweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_post_id", nullable = false)
    private CommunityPost originalPost;

    @Column(name = "quote_content", length = 280)
    private String quoteContent; // null for simple retweet, has content for quote tweet

    @Column(name = "is_quote_tweet")
    private Boolean isQuoteTweet = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isQuoteTweet == null) {
            this.isQuoteTweet = (this.quoteContent != null && !this.quoteContent.trim().isEmpty());
        }
    }

    // Constructors
    public PostRetweet() {}

    public PostRetweet(User user, CommunityPost originalPost) {
        this.user = user;
        this.originalPost = originalPost;
        this.isQuoteTweet = false;
    }

    public PostRetweet(User user, CommunityPost originalPost, String quoteContent) {
        this.user = user;
        this.originalPost = originalPost;
        this.quoteContent = quoteContent;
        this.isQuoteTweet = (quoteContent != null && !quoteContent.trim().isEmpty());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public CommunityPost getOriginalPost() { return originalPost; }
    public void setOriginalPost(CommunityPost originalPost) { this.originalPost = originalPost; }

    public String getQuoteContent() { return quoteContent; }
    public void setQuoteContent(String quoteContent) { this.quoteContent = quoteContent; }

    public Boolean getIsQuoteTweet() { return isQuoteTweet; }
    public void setIsQuoteTweet(Boolean isQuoteTweet) { this.isQuoteTweet = isQuoteTweet; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
