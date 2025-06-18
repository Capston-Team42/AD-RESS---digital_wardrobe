package com.example.digital_wardrobe.repository;

import com.example.digital_wardrobe.model.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, String> {

    // 특정 글(postId)에 달린 댓글 목록 조회
	Page<Comment> findByPostId(String postId, Pageable pageable);
    Optional<Comment> findByCommentIdAndUsername(String commentId, String userId);
}
