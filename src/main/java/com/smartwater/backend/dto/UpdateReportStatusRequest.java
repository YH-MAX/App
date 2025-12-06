package com.smartwater.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateReportStatusRequest {

    @NotBlank(message = "Status is required.")
    private String status; // OPEN / IN_REVIEW / RESOLVED

    @Size(max = 1000, message = "Admin comment must be at most 1000 characters.")
    private String adminComment;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminComment() { return adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }
}
