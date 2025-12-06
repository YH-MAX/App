package com.smartwater.backend.dto;

import java.time.LocalDateTime;

public class CommunityReplyResponse {

    private Long id;
    private Long postId;

    private String authorName;
    private String authorEmail;

    private String content;
    private Boolean expertReply;

    private LocalDateTime createdAt;

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getExpertReply() { return expertReply; }
    public void setExpertReply(Boolean expertReply) { this.expertReply = expertReply; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
