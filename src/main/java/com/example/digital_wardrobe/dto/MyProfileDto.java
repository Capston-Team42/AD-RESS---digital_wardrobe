package com.example.digital_wardrobe.dto;

import com.example.digital_wardrobe.model.Post;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyProfileDto {
    private String username;
    private List<Post> myPosts;
    private List<Post> likedPosts;
}
