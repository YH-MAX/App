package com.smartwater.backend.controller;

import com.smartwater.backend.dto.CommunityPostRequest;
import com.smartwater.backend.dto.CommunityPostResponse;
import com.smartwater.backend.dto.CommunityReplyRequest;
import com.smartwater.backend.dto.CommunityReplyResponse;
import com.smartwater.backend.dto.PageResponse;
import com.smartwater.backend.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*")
public class CommunityController {

    @Autowired
    private CommunityService communityService;



    @PostMapping("/posts")
    public CommunityPostResponse createPost(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody @Valid CommunityPostRequest body
    ) {
        String email = currentUser.getUsername();
        return communityService.createPost(email, body);
    }


    @GetMapping("/posts")
    public PageResponse<CommunityPostResponse> getPosts(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return communityService.getPostsPaged(page, size, location);
    }


    @GetMapping("/posts/{id}")
    public CommunityPostResponse getPostById(@PathVariable Long id) {
        return communityService.getPostDetail(id);
    }



    @PostMapping("/posts/{postId}/like")
    public CommunityPostResponse likePost(@PathVariable Long postId) {
        return communityService.likePost(postId);
    }



    @PostMapping("/posts/{postId}/replies")
    public CommunityReplyResponse addReply(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody @Valid CommunityReplyRequest body
    ) {
        String email = currentUser.getUsername();
        return communityService.addReplyToPost(postId, email, body);
    }


    @GetMapping("/posts/{postId}/replies")
    public PageResponse<CommunityReplyResponse> getRepliesForPost(
            @PathVariable Long postId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return communityService.getRepliesForPostPaged(postId, page, size);
    }



    @DeleteMapping("/posts/{postId}")
    public void deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String email = currentUser.getUsername();
        communityService.deletePost(postId, email);
    }

    @DeleteMapping("/replies/{replyId}")
    public void deleteReply(
            @PathVariable Long replyId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String email = currentUser.getUsername();
        communityService.deleteReply(replyId, email);
    }
}
