package com.smartwater.backend.repository;

import com.smartwater.backend.model.CommunityPost;
import com.smartwater.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    List<CommunityPost> findAllByOrderByCreatedAtDesc();

    List<CommunityPost> findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(String location);

    Page<CommunityPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<CommunityPost> findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(
            String location,
            Pageable pageable
    );

    // Search posts by content (for hashtag and keyword search)
    Page<CommunityPost> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String content,
            Pageable pageable
    );

    // Get posts by a specific user
    Page<CommunityPost> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Count posts by user
    long countByUser(User user);
}
