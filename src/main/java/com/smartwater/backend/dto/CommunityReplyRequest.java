package com.smartwater.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class CommunityReplyRequest {


    @NotBlank(message = "Reply content is required")
    @Size(max = 1000, message = "Reply must be at most 1000 characters")
    private String content;


    private Boolean expertReply;

    // ===== Getter / Setter =====

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getExpertReply() {
        return expertReply;
    }

    public void setExpertReply(Boolean expertReply) {
        this.expertReply = expertReply;
    }
}
