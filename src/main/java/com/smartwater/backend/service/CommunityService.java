package com.smartwater.backend.service;

import com.smartwater.backend.dto.CommunityPostRequest;
import com.smartwater.backend.dto.CommunityPostResponse;
import com.smartwater.backend.dto.CommunityReplyRequest;
import com.smartwater.backend.dto.CommunityReplyResponse;
import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.CommunityReply;
import com.smartwater.backend.model.User;
import com.smartwater.backend.repository.CommunityPostRepository;
import com.smartwater.backend.repository.CommunityReplyRepository;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.smartwater.backend.dto.PageResponse;
import com.smartwater.backend.dto.CommunityPostResponse;
import com.smartwater.backend.dto.CommunityReplyResponse;
import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.CommunityReply;

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
            String name = post.getUser().getFirstName();

            dto.setAuthorName(name);
            dto.setAuthorEmail(post.getUser().getEmail());
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

}
