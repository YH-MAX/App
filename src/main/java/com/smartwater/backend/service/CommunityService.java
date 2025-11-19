package com.smartwater.backend.service;

import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.CommunityReply;
import com.smartwater.backend.model.User;
import com.smartwater.backend.repository.CommunityPostRepository;
import com.smartwater.backend.repository.CommunityReplyRepository;
import com.smartwater.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private CommunityReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;


    public CommunityPost createPost(
            String userEmail,
            String content,
            String photoUrl,
            String location,
            Double ph,
            Double temperature,
            Double turbidity,
            String type
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setContent(content);
        post.setPhotoUrl(photoUrl);
        post.setLocation(location);
        post.setPh(ph);
        post.setTemperature(temperature);
        post.setTurbidity(turbidity);


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

        return postRepository.save(post);
    }


    public List<CommunityPost> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }


    public List<CommunityPost> getPostsByLocation(String locationQuery) {
        return postRepository.findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(locationQuery);
    }


    public CommunityPost getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }


    public CommunityPost likePost(Long postId) {
        CommunityPost post = getPostById(postId);
        Integer currentLikes = post.getLikes();
        if (currentLikes == null) {
            currentLikes = 0;
        }
        post.setLikes(currentLikes + 1);
        return postRepository.save(post);
    }


    public List<CommunityReply> getRepliesForPost(Long postId) {
        CommunityPost post = getPostById(postId);
        return replyRepository.findByPostOrderByCreatedAtAsc(post);
    }


    public CommunityReply addReplyToPost(
            Long postId,
            String userEmail,
            String content,
            boolean expertReply
    ) {
        CommunityPost post = getPostById(postId);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        CommunityReply reply = new CommunityReply();
        reply.setPost(post);
        reply.setUser(user);
        reply.setContent(content);
        reply.setExpertReply(expertReply);

        return replyRepository.save(reply);
    }
}
