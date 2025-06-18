package com.example.digital_wardrobe.repository;

import com.example.digital_wardrobe.model.Post;

import jakarta.validation.constraints.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    // 특정 userId로 글 목록 조회 (내 글 모아보기)
    List<Post> findByUsername(String userId);

    // 특정 해시태그가 포함된 글 목록 조회 (검색용)
    List<Post> findByHashtagsContaining(String hashtag);
    
 // 최신순 정렬
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 인기순 정렬
    Page<Post> findAllByOrderByLikeCountDesc(Pageable pageable);
}



