package com.example.digital_wardrobe.service;

import com.example.digital_wardrobe.dto.MyProfileDto;
import com.example.digital_wardrobe.dto.UserProfileDto;
import com.example.digital_wardrobe.model.LikePost;
import com.example.digital_wardrobe.model.Post;
import com.example.digital_wardrobe.model.User;
import com.example.digital_wardrobe.repository.LikePostRepository;
import com.example.digital_wardrobe.repository.PostRepository;
import com.example.digital_wardrobe.repository.team.UserRepository;

import java.io.IOException;  
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikePostRepository likePostRepository;

    // ✅ 내 프로필 조회
    public MyProfileDto getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> myPosts = postRepository.findByUsername(username);

        List<String> likedPostIds = likePostRepository.findByUsername(username)
                .stream()
                .map(LikePost::getPostId)
                .collect(Collectors.toList());

        List<Post> likedPosts = postRepository.findAllById(likedPostIds);

        return MyProfileDto.builder()
                .username(user.getUsername())
                .myPosts(myPosts)
                .likedPosts(likedPosts)
                .build();
    }

    // ✅ 다른 유저 프로필 조회
    public UserProfileDto getOtherUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Post> posts = postRepository.findByUsername(username);

        return UserProfileDto.builder()
                .username(user.getUsername())
                .posts(posts)
                .build();
    }
}


