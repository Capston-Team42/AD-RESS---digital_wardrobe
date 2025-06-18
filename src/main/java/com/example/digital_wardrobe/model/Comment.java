package com.example.digital_wardrobe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    private String commentId;

    private String postId;   // 어떤 글의 댓글인지

    private String username;   // 작성자 이름(고유값)

    private String content;  // 댓글 내용

    private LocalDateTime createdAt = LocalDateTime.now();
}
