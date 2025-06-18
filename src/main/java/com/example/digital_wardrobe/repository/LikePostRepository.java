package com.example.digital_wardrobe.repository;

import com.example.digital_wardrobe.model.LikePost;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LikePostRepository extends MongoRepository<LikePost, String> {
    boolean existsByPostIdAndUsername(String postId, String userId);
    Optional<LikePost> findByPostIdAndUsername(String postId, String userId);
    void deleteByPostIdAndUsername(String postId, String userId);
    long countByPostId(String postId);
    List<LikePost> findByUsername(String username);

}

