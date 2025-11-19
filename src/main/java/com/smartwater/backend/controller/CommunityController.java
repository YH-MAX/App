package com.smartwater.backend.controller;

import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.CommunityReply;
import com.smartwater.backend.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping("/posts")
    public CommunityPost createPost(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody Map<String, Object> body
    ) {
        String email = currentUser.getUsername();

        String content = (String) body.get("content");
        String photoUrl = (String) body.get("photoUrl");
        String location = (String) body.get("location");

        Double ph = body.get("ph") instanceof Number ? ((Number) body.get("ph")).doubleValue() : null;
        Double temperature = body.get("temperature") instanceof Number ? ((Number) body.get("temperature")).doubleValue() : null;
        Double turbidity = body.get("turbidity") instanceof Number ? ((Number) body.get("turbidity")).doubleValue() : null;

        String type = (String) body.get("type");

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Content is required.");
        }

        return communityService.createPost(
                email, content, photoUrl, location, ph, temperature, turbidity, type
        );
    }

    @GetMapping("/posts")
    public List<CommunityPost> getPosts(@RequestParam(required = false) String location) {
        if (location != null && !location.trim().isEmpty()) {
            return communityService.getPostsByLocation(location.trim());
        }
        return communityService.getAllPosts();
    }

    @GetMapping("/posts/{id}")
    public CommunityPost getPostById(@PathVariable Long id) {
        return communityService.getPostById(id);
    }


    @PostMapping("/posts/{postId}/like")
    public CommunityPost likePost(@PathVariable Long postId) {
        
        return communityService.likePost(postId);
    }

    @PostMapping("/posts/{postId}/replies")
    public CommunityReply addReply(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody Map<String, Object> body
    ) {
        String email = currentUser.getUsername();
        String content = (String) body.get("content");

        Boolean expertReply = body.get("expertReply") instanceof Boolean
                ? (Boolean) body.get("expertReply")
                : false;

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Reply content is required.");
        }

        return communityService.addReplyToPost(postId, email, content, expertReply);
    }

    @GetMapping("/posts/{postId}/replies")
    public List<CommunityReply> getRepliesForPost(@PathVariable Long postId) {
        return communityService.getRepliesForPost(postId);
    }
}
