package com.smartwater.backend.repository;

import com.smartwater.backend.model.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {


    List<CommunityPost> findAllByOrderByCreatedAtDesc();


    List<CommunityPost> findByLocationContainingIgnoreCaseOrderByCreatedAtDesc(String location);
}
