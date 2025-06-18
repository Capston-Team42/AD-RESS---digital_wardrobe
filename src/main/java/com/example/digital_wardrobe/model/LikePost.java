package com.example.digital_wardrobe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "like_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikePost {

    @Id
    private String likeId;

    private String username;   // 누가 좋아요 눌렀는지

    private String postId;   // 어떤 글을 좋아요했는지

    private LocalDateTime createdAt = LocalDateTime.now();
}
