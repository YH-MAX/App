package com.smartwater.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_posts")
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;


    @Column(nullable = false, length = 1000)
    private String content;


    @Column(name = "photo_url")
    private String photoUrl;


    @Column(length = 255)
    private String location;


    @Column(name = "type", length = 20)
    private String type; // 默认 INFO


    private Double ph;
    private Double temperature;
    private Double turbidity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "likes")
    private Integer likes;

    // Twitter-like engagement fields
    @Column(name = "retweet_count")
    private Integer retweetCount = 0;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "bookmark_count")
    private Integer bookmarkCount = 0;

    @Column(name = "is_retweet")
    private Boolean isRetweet = false;

    @Column(name = "original_post_id")
    private Long originalPostId;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CommunityReply> replies = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.type == null || this.type.isBlank()) {
            this.type = "INFO";
        }
        if (this.likes == null) {
            this.likes = 0;
        }
        if (this.retweetCount == null) {
            this.retweetCount = 0;
        }
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        if (this.bookmarkCount == null) {
            this.bookmarkCount = 0;
        }
        if (this.isRetweet == null) {
            this.isRetweet = false;
        }
    }

    // ===== Getter / Setter =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getTurbidity() { return turbidity; }
    public void setTurbidity(Double turbidity) { this.turbidity = turbidity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }

    public List<CommunityReply> getReplies() { return replies; }
    public void setReplies(List<CommunityReply> replies) { this.replies = replies; }

    // Twitter-like engagement getters/setters
    public Integer getRetweetCount() { return retweetCount != null ? retweetCount : 0; }
    public void setRetweetCount(Integer retweetCount) { this.retweetCount = retweetCount; }

    public Integer getViewCount() { return viewCount != null ? viewCount : 0; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getBookmarkCount() { return bookmarkCount != null ? bookmarkCount : 0; }
    public void setBookmarkCount(Integer bookmarkCount) { this.bookmarkCount = bookmarkCount; }

    public Boolean getIsRetweet() { return isRetweet != null ? isRetweet : false; }
    public void setIsRetweet(Boolean isRetweet) { this.isRetweet = isRetweet; }

    public Long getOriginalPostId() { return originalPostId; }
    public void setOriginalPostId(Long originalPostId) { this.originalPostId = originalPostId; }
}
