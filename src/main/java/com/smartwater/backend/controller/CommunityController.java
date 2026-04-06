package com.smartwater.backend.controller;

import com.smartwater.backend.dto.CommunityPostRequest;
import com.smartwater.backend.dto.CommunityPostResponse;
import com.smartwater.backend.dto.CommunityReplyRequest;
import com.smartwater.backend.dto.CommunityReplyResponse;
import com.smartwater.backend.dto.PageResponse;
import com.smartwater.backend.service.CommunityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        String email = currentUser != null ? currentUser.getUsername() : null;
        if (email != null) {
            communityService.setCurrentUser(email);
        }
        PageResponse<CommunityPostResponse> result = communityService.getPostsPaged(page, size, location);
        if (email != null) {
            communityService.clearCurrentUser();
        }
        return result;
    }

    @GetMapping("/posts/{id}")
    public CommunityPostResponse getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        // Increment view count
        communityService.incrementViewCount(id);
        
        String email = currentUser != null ? currentUser.getUsername() : null;
        if (email != null) {
            communityService.setCurrentUser(email);
        }
        CommunityPostResponse result = communityService.getPostDetail(id);
        if (email != null) {
            communityService.clearCurrentUser();
        }
        return result;
    }

    // ==================== TWITTER-LIKE ENDPOINTS ====================

    /**
     * Toggle like on a post (like/unlike)
     * POST /api/community/posts/{postId}/like
     */
    @PostMapping("/posts/{postId}/like")
    public CommunityPostResponse toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return communityService.toggleLike(postId, currentUser.getUsername());
    }

    /**
     * Toggle bookmark on a post
     * POST /api/community/posts/{postId}/bookmark
     */
    @PostMapping("/posts/{postId}/bookmark")
    public CommunityPostResponse toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return communityService.toggleBookmark(postId, currentUser.getUsername());
    }

    /**
     * Retweet a post
     * POST /api/community/posts/{postId}/retweet
     */
    @PostMapping("/posts/{postId}/retweet")
    public CommunityPostResponse retweet(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return communityService.retweet(postId, currentUser.getUsername());
    }

    /**
     * Undo retweet
     * DELETE /api/community/posts/{postId}/retweet
     */
    @DeleteMapping("/posts/{postId}/retweet")
    public CommunityPostResponse undoRetweet(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return communityService.undoRetweet(postId, currentUser.getUsername());
    }

    /**
     * Quote tweet
     * POST /api/community/posts/{postId}/quote
     */
    @PostMapping("/posts/{postId}/quote")
    public CommunityPostResponse quoteTweet(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestBody Map<String, String> body
    ) {
        String quoteContent = body.get("content");
        return communityService.quoteTweet(postId, currentUser.getUsername(), quoteContent);
    }

    /**
     * Get current user's bookmarks
     * GET /api/community/bookmarks
     */
    @GetMapping("/bookmarks")
    public PageResponse<CommunityPostResponse> getMyBookmarks(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return communityService.getUserBookmarks(currentUser.getUsername(), page, size);
    }

    /**
     * Get current user's liked posts
     * GET /api/community/likes
     */
    @GetMapping("/likes")
    public PageResponse<CommunityPostResponse> getMyLikedPosts(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return communityService.getUserLikedPosts(currentUser.getUsername(), page, size);
    }

    /**
     * Search posts
     * GET /api/community/search
     */
    @GetMapping("/search")
    public PageResponse<CommunityPostResponse> searchPosts(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        String email = currentUser != null ? currentUser.getUsername() : null;
        return communityService.searchPosts(query, email, page, size);
    }

    /**
     * Get posts by user ID
     * GET /api/community/users/{userId}/posts
     */
    @GetMapping("/users/{userId}/posts")
    public PageResponse<CommunityPostResponse> getPostsByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        String email = currentUser != null ? currentUser.getUsername() : null;
        return communityService.getPostsByUser(userId, email, page, size);
    }

    // ==================== EXISTING ENDPOINTS ====================

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
