package com.smartwater.backend.service;

import com.smartwater.backend.dto.CommunityPostRequest;
import com.smartwater.backend.dto.CommunityPostResponse;
import com.smartwater.backend.dto.CommunityReplyRequest;
import com.smartwater.backend.dto.CommunityReplyResponse;
import com.smartwater.backend.model.*;
import com.smartwater.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.smartwater.backend.dto.PageResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private CommunityReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostLikeRepository likeRepository;

    @Autowired
    private PostBookmarkRepository bookmarkRepository;

    @Autowired
    private PostRetweetRepository retweetRepository;

    // Current user context for enriching responses
    private ThreadLocal<User> currentUserContext = new ThreadLocal<>();

    public void setCurrentUser(String email) {
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(currentUserContext::set);
        }
    }

    public void clearCurrentUser() {
        currentUserContext.remove();
    }

    private CommunityPost findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private CommunityPostResponse toPostResponse(CommunityPost post) {
        CommunityPostResponse dto = new CommunityPostResponse();
        dto.setId(post.getId());

        if (post.getUser() != null) {
            User author = post.getUser();
            dto.setAuthorId(author.getId());
            dto.setAuthorName(author.getFirstName());
            dto.setAuthorEmail(author.getEmail());
            dto.setAuthorProfileImageUrl(author.getProfileImageUrl());
            dto.setAuthorIsExpert(author.isExpert());
        }

        dto.setContent(post.getContent());
        dto.setPhotoUrl(post.getPhotoUrl());
        dto.setLocation(post.getLocation());
        dto.setType(post.getType());
        dto.setPh(post.getPh());
        dto.setTemperature(post.getTemperature());
        dto.setTurbidity(post.getTurbidity());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLikes(post.getLikes());

        int replyCount = post.getReplies() == null ? 0 : post.getReplies().size();
        dto.setReplyCount(replyCount);

        // Twitter-like engagement counts
        dto.setRetweetCount(post.getRetweetCount());
        dto.setViewCount(post.getViewCount());
        dto.setBookmarkCount(post.getBookmarkCount());
        dto.setIsRetweet(post.getIsRetweet());
        dto.setOriginalPostId(post.getOriginalPostId());

        // Current user's interaction status
        User currentUser = currentUserContext.get();
        if (currentUser != null) {
            dto.setIsLikedByCurrentUser(likeRepository.existsByUserAndPost(currentUser, post));
            dto.setIsRetweetedByCurrentUser(retweetRepository.existsByUserAndOriginalPostAndIsQuoteTweetFalse(currentUser, post));
            dto.setIsBookmarkedByCurrentUser(bookmarkRepository.existsByUserAndPost(currentUser, post));
        } else {
            dto.setIsLikedByCurrentUser(false);
            dto.setIsRetweetedByCurrentUser(false);
            dto.setIsBookmarkedByCurrentUser(false);
        }

        return dto;
    }

    private CommunityReplyResponse toReplyResponse(CommunityReply reply) {
        CommunityReplyResponse dto = new CommunityReplyResponse();
        dto.setId(reply.getId());
        dto.setPostId(reply.getPost().getId());

        if (reply.getUser() != null) {
            dto.setAuthorName(reply.getUser().getFirstName());
            dto.setAuthorEmail(reply.getUser().getEmail());
        }

        dto.setContent(reply.getContent());
        dto.setExpertReply(reply.getExpertReply());
        dto.setCreatedAt(reply.getCreatedAt());

        return dto;
    }



    public CommunityPostResponse createPost(String userEmail, CommunityPostRequest req) {
        User user = findUserOrThrow(userEmail);


        boolean hasContent = req.getContent() != null && !req.getContent().trim().isEmpty();
        boolean hasPhoto = req.getPhotoUrl() != null && !req.getPhotoUrl().trim().isEmpty();
        boolean hasReading =
                req.getPh() != null || req.getTemperature() != null || req.getTurbidity() != null;

        if (!hasContent && !hasPhoto && !hasReading) {
            throw new IllegalArgumentException(
                    "Post must contain text, a photo, or at least one water reading.");
        }

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setContent(hasContent ? req.getContent().trim() : null);
        post.setPhotoUrl(req.getPhotoUrl());
        post.setLocation(req.getLocation());
        post.setPh(req.getPh());
        post.setTemperature(req.getTemperature());
        post.setTurbidity(req.getTurbidity());


        String type = req.getType();
        String finalType;
        if (type == null || type.isBlank()) {
            finalType = "INFO";
        } else {
            String upper = type.trim().toUpperCase();
            switch (upper) {
                case "ALERT":
                case "QUESTION":
                case "INFO":
                    finalType = upper;
                    break;
                default:
                    finalType = "INFO";
            }
        }
        post.setType(finalType);

        CommunityPost saved = postRepository.save(post);
        return toPostResponse(saved);
    }



    public List<CommunityPostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toPostResponse)
                .collect(Collectors.toList());
    }

    public List<CommunityPostResponse> getPostsByLocation(String locationQuery) {
        return postRepository.findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(locationQuery)
                .stream()
                .map(this::toPostResponse)
                .collect(Collectors.toList());
    }

    public CommunityPostResponse getPostDetail(Long id) {
        CommunityPost post = findPostOrThrow(id);
        return toPostResponse(post);
    }



    public CommunityPostResponse likePost(Long postId) {
        CommunityPost post = findPostOrThrow(postId);
        Integer currentLikes = post.getLikes();
        if (currentLikes == null) {
            currentLikes = 0;
        }
        post.setLikes(currentLikes + 1);
        CommunityPost saved = postRepository.save(post);
        return toPostResponse(saved);
    }



    public List<CommunityReplyResponse> getRepliesForPost(Long postId) {
        CommunityPost post = findPostOrThrow(postId);
        List<CommunityReply> replies = replyRepository.findByPostOrderByCreatedAtAsc(post);
        return replies.stream()
                .map(this::toReplyResponse)
                .collect(Collectors.toList());
    }

    public CommunityReplyResponse addReplyToPost(
            Long postId,
            String userEmail,
            CommunityReplyRequest req
    ) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userEmail);

        CommunityReply reply = new CommunityReply();
        reply.setPost(post);
        reply.setUser(user);
        reply.setContent(req.getContent().trim());

        boolean wantExpert = Boolean.TRUE.equals(req.getExpertReply());
        boolean isExpert = user.isExpert();   // 只有专家/管理员才可以被标为 expert reply
        reply.setExpertReply(wantExpert && isExpert);

        CommunityReply saved = replyRepository.save(reply);
        return toReplyResponse(saved);
    }



    public void deletePost(Long postId, String userEmail) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userEmail);

        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdminOrExpert = user.isExpert(); // 你可以再加 isAdmin()

        if (!isOwner && !isAdminOrExpert) {
            throw new RuntimeException("You are not allowed to delete this post.");
        }

        postRepository.delete(post);
    }

    public void deleteReply(Long replyId, String userEmail) {
        CommunityReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found with id: " + replyId));

        User user = findUserOrThrow(userEmail);

        boolean isOwner = reply.getUser().getId().equals(user.getId());
        boolean isAdminOrExpert = user.isExpert();

        if (!isOwner && !isAdminOrExpert) {
            throw new RuntimeException("You are not allowed to delete this reply.");
        }

        replyRepository.delete(reply);
    }


    public PageResponse<CommunityPostResponse> getPostsPaged(Integer page, Integer size, String location) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 10 : size; // 限制最大50

        Pageable pageable = PageRequest.of(p, s);

        Page<CommunityPost> pageResult;
        if (location != null && !location.trim().isEmpty()) {
            pageResult = postRepository
                    .findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(location.trim(), pageable);
        } else {
            pageResult = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        PageResponse<CommunityPostResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(this::toPostResponse)
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        return resp;
    }


    public PageResponse<CommunityReplyResponse> getRepliesForPostPaged(Long postId, Integer page, Integer size) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 10 : size;

        Pageable pageable = PageRequest.of(p, s);

        CommunityPost post = findPostOrThrow(postId);
        Page<CommunityReply> pageResult = replyRepository
                .findByPostOrderByCreatedAtAsc(post, pageable);

        PageResponse<CommunityReplyResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(this::toReplyResponse)
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        return resp;
    }

    // ==================== TWITTER-LIKE FEATURES ====================

    /**
     * Toggle like on a post (like if not liked, unlike if already liked)
     */
    @Transactional
    public CommunityPostResponse toggleLike(Long postId, String userEmail) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userEmail);

        if (likeRepository.existsByUserAndPost(user, post)) {
            // Unlike
            likeRepository.deleteByUserAndPost(user, post);
            post.setLikes(Math.max(0, post.getLikes() - 1));
        } else {
            // Like
            PostLike like = new PostLike(user, post);
            likeRepository.save(like);
            post.setLikes(post.getLikes() + 1);
        }

        postRepository.save(post);
        setCurrentUser(userEmail);
        CommunityPostResponse response = toPostResponse(post);
        clearCurrentUser();
        return response;
    }

    /**
     * Toggle bookmark on a post
     */
    @Transactional
    public CommunityPostResponse toggleBookmark(Long postId, String userEmail) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userEmail);

        boolean wasBookmarked = bookmarkRepository.existsByUserAndPost(user, post);

        if (wasBookmarked) {
            // Remove bookmark
            bookmarkRepository.deleteByUserAndPost(user, post);
            post.setBookmarkCount(Math.max(0, post.getBookmarkCount() - 1));
        } else {
            // Add bookmark
            PostBookmark bookmark = new PostBookmark(user, post);
            bookmarkRepository.save(bookmark);
            post.setBookmarkCount(post.getBookmarkCount() + 1);
        }

        postRepository.save(post);
        setCurrentUser(userEmail);
        CommunityPostResponse response = toPostResponse(post);
        clearCurrentUser();
        return response;
    }

    /**
     * Retweet a post (simple retweet)
     */
    @Transactional
    public CommunityPostResponse retweet(Long postId, String userEmail) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userEmail);

        // Check if already retweeted
        if (retweetRepository.existsByUserAndOriginalPostAndIsQuoteTweetFalse(user, post)) {
            throw new RuntimeException("You have already retweeted this post");
        }

        PostRetweet retweet = new PostRetweet(user, post);
        retweetRepository.save(retweet);

        post.setRetweetCount(post.getRetweetCount() + 1);
        postRepository.save(post);

        setCurrentUser(userEmail);
        CommunityPostResponse response = toPostResponse(post);
        clearCurrentUser();
        return response;
    }

    /**
     * Undo retweet
     */
    @Transactional
    public CommunityPostResponse undoRetweet(Long postId, String userEmail) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userEmail);

        if (!retweetRepository.existsByUserAndOriginalPostAndIsQuoteTweetFalse(user, post)) {
            throw new RuntimeException("You have not retweeted this post");
        }

        retweetRepository.deleteByUserAndOriginalPostAndIsQuoteTweetFalse(user, post);
        post.setRetweetCount(Math.max(0, post.getRetweetCount() - 1));
        postRepository.save(post);

        setCurrentUser(userEmail);
        CommunityPostResponse response = toPostResponse(post);
        clearCurrentUser();
        return response;
    }

    /**
     * Quote tweet - retweet with added comment
     */
    @Transactional
    public CommunityPostResponse quoteTweet(Long originalPostId, String userEmail, String quoteContent) {
        CommunityPost originalPost = findPostOrThrow(originalPostId);
        User user = findUserOrThrow(userEmail);

        if (quoteContent == null || quoteContent.trim().isEmpty()) {
            throw new RuntimeException("Quote content cannot be empty");
        }

        PostRetweet quote = new PostRetweet(user, originalPost, quoteContent.trim());
        retweetRepository.save(quote);

        originalPost.setRetweetCount(originalPost.getRetweetCount() + 1);
        postRepository.save(originalPost);

        // Return the original post with updated count
        setCurrentUser(userEmail);
        CommunityPostResponse response = toPostResponse(originalPost);
        clearCurrentUser();
        return response;
    }

    /**
     * Get user's bookmarks
     */
    public PageResponse<CommunityPostResponse> getUserBookmarks(String userEmail, Integer page, Integer size) {
        User user = findUserOrThrow(userEmail);

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 20 : size;
        Pageable pageable = PageRequest.of(p, s);

        Page<PostBookmark> pageResult = bookmarkRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        setCurrentUser(userEmail);
        PageResponse<CommunityPostResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(b -> toPostResponse(b.getPost()))
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        clearCurrentUser();
        return resp;
    }

    /**
     * Get user's liked posts
     */
    public PageResponse<CommunityPostResponse> getUserLikedPosts(String userEmail, Integer page, Integer size) {
        User user = findUserOrThrow(userEmail);

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 20 : size;
        Pageable pageable = PageRequest.of(p, s);

        Page<PostLike> pageResult = likeRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        setCurrentUser(userEmail);
        PageResponse<CommunityPostResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(l -> toPostResponse(l.getPost()))
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        clearCurrentUser();
        return resp;
    }

    /**
     * Increment view count for a post
     */
    @Transactional
    public void incrementViewCount(Long postId) {
        CommunityPost post = findPostOrThrow(postId);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    /**
     * Search posts by content or hashtag
     */
    public PageResponse<CommunityPostResponse> searchPosts(String query, String userEmail, Integer page, Integer size) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 20 : size;
        Pageable pageable = PageRequest.of(p, s);

        Page<CommunityPost> pageResult = postRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(query, pageable);

        if (userEmail != null) {
            setCurrentUser(userEmail);
        }
        PageResponse<CommunityPostResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(this::toPostResponse)
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        if (userEmail != null) {
            clearCurrentUser();
        }
        return resp;
    }

    /**
     * Get posts by user ID
     */
    public PageResponse<CommunityPostResponse> getPostsByUser(Long userId, String currentUserEmail, Integer page, Integer size) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 50) ? 20 : size;
        Pageable pageable = PageRequest.of(p, s);

        Page<CommunityPost> pageResult = postRepository.findByUserOrderByCreatedAtDesc(author, pageable);

        if (currentUserEmail != null) {
            setCurrentUser(currentUserEmail);
        }
        PageResponse<CommunityPostResponse> resp = new PageResponse<>();
        resp.setItems(pageResult.getContent().stream()
                .map(this::toPostResponse)
                .toList());
        resp.setPage(pageResult.getNumber());
        resp.setSize(pageResult.getSize());
        resp.setTotalElements(pageResult.getTotalElements());
        resp.setTotalPages(pageResult.getTotalPages());
        resp.setLast(pageResult.isLast());
        if (currentUserEmail != null) {
            clearCurrentUser();
        }
        return resp;
    }
}
