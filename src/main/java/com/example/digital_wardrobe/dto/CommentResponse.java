package com.example.digital_wardrobe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private String commentId;
    private String postId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
}
