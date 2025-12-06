package com.smartwater.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateReportRequest {

    @NotBlank(message = "Description is required.")
    @Size(max = 1000, message = "Description must be at most 1000 characters.")
    private String description;

    @Size(max = 500, message = "Photo URL is too long.")
    private String photoUrl;

    @Size(max = 255, message = "Location is too long.")
    private String location;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
