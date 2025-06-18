package com.example.digital_wardrobe.service;

import com.example.digital_wardrobe.dto.CommentResponse;
import com.example.digital_wardrobe.model.Comment;
import com.example.digital_wardrobe.model.Post;
import com.example.digital_wardrobe.repository.CommentRepository;
import com.example.digital_wardrobe.repository.PostRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    /**
     * 댓글 등록, post 모델의 댓글 수 +! 업로드 & 요청반환시 +1반영된 댓글 수를 같이 반환
     */
    public Map<String, Object> addComment(Comment comment) {
        Comment saved = commentRepository.save(comment);

        long count = 0;
        if (comment.getPostId() != null) {
            postRepository.findById(comment.getPostId()).ifPresent(post -> {
                post.setCommentCount(post.getCommentCount() + 1);
                postRepository.save(post);
            });
            count = postRepository.findById(comment.getPostId())
                                  .map(Post::getCommentCount)
                                  .orElse(0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("comment", saved);
        result.put("commentCount", count);
        return result;
    }



    /**
     * 특정 게시글에 달린 댓글 목록 조회(페이징 적용)
     */
    public Page<CommentResponse> getPagedCommentsByPostId(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Comment> commentsPage = commentRepository.findByPostId(postId, pageable);

        // UserProfile 조회 없이 Comment 정보만 반환
        return commentsPage.map(comment -> CommentResponse.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .username(comment.getUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build());
    }

    /**
     * 댓글 삭제 - 작성자 본인만 삭제 가능, 삭제 후 post 모델에서 댓글 수 -1 & 요청 반환시 -1 반영된 댓글 수를 함께 반환
     */
    public Map<String, Object> deleteComment(String commentId, String userId) {
        Optional<Comment> optionalComment = commentRepository.findByCommentIdAndUsername(commentId, userId);
        Map<String, Object> result = new HashMap<>();

        if (optionalComment.isEmpty()) {
            result.put("success", false);
            return result;
        }

        Comment comment = optionalComment.get();
        commentRepository.delete(comment);

        long count = 0;
        if (comment.getPostId() != null) {
            postRepository.findById(comment.getPostId()).ifPresent(post -> {
                post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
                postRepository.save(post);
            });
            count = postRepository.findById(comment.getPostId())
                                  .map(Post::getCommentCount)
                                  .orElse(0);
        }

        result.put("success", true);
        result.put("deletedCommentId", commentId);
        result.put("commentCount", count);
        return result;
    }


}
