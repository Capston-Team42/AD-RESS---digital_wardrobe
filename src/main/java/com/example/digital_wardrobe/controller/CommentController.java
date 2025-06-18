package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.dto.CommentResponse;
import com.example.digital_wardrobe.model.Comment;
import com.example.digital_wardrobe.service.CommentService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성 - username은 토큰에서 가져옴
     */
    @PostMapping
    public Map<String, Object> createComment(@RequestBody Comment comment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        comment.setUsername(username);
        return commentService.addComment(comment);
    }

    /**
     * 특정 글에 달린 댓글 목록 조회
     */
    @GetMapping("/post/{postId}")
    public Page<CommentResponse> getPagedCommentsByPostId(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentService.getPagedCommentsByPostId(postId, page, size);
    }

    /**
     * 댓글 삭제 - username은 토큰에서 가져옴
     */
    @DeleteMapping("/{commentId}")
    public Map<String, Object> removeComment(@PathVariable String commentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return commentService.deleteComment(commentId, username);
    }
}

