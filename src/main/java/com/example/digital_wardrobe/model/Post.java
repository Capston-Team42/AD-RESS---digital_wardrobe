package com.example.digital_wardrobe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")  // MongoDB용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    private String postId;  // MongoDB는 보통 String 타입 ID 사용

    private String username;  // 작성자 ID

    private List<String> photoUrls = new ArrayList<>();  // ✅ 수정됨

    private String content;  // 글 내용

    private List<String> hashtags = new ArrayList<>();  // 해시태그 리스트
    
    private int likeCount = 0;     // 좋아요 수 :누르면 ++ 취쇠하면 -- 
    
    private int commentCount = 0;  // 댓글 수

    private LocalDateTime createdAt = LocalDateTime.now();
}


