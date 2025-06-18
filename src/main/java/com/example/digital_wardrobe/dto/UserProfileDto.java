package com.example.digital_wardrobe.dto;

import com.example.digital_wardrobe.model.Post;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
    private String username;
    private List<Post> posts;
}
