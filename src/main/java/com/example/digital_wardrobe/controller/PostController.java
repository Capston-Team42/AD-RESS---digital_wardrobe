package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.model.Post;
import com.example.digital_wardrobe.service.PostService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 글 작성 (사진 포함, 여러장 가능)
     */
    @PostMapping("/upload")
    public Post createPostWithImages(
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @RequestPart("post") String postJson) throws IOException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Post postRequest = objectMapper.readValue(postJson, Post.class);
        postRequest.setUsername(username);  // 작성자 ID 설정

        return postService.createPostWithImages(imageFiles, postRequest);
    }

    /**
     * 글 목록 조회 (정렬 + 페이징)
     */
    @GetMapping
    public Page<Post> getPosts(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postService.getPosts(sort, page, size);
    }

    /**
     * 글 상세 조회
     */
    @GetMapping("/{postId}")
    public Optional<Post> getPost(@PathVariable String postId) {
        return postService.getPostById(postId);
    }

    /**
     * 글 수정 (사진 교체 포함, 작성자만 가능)
     */
    @PutMapping("/{postId}/update-with-images")
    public Optional<Post> updatePostWithImages(
            @PathVariable String postId,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImageFiles,
            @RequestPart("keepImageUrls") String keepImageUrlsJson,  // ✅ String으로 받기
            @RequestPart("post") String updatedPostJson) throws IOException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("🔑 요청자 username (from token): " + username);

        // ✅ JSON 문자열 ➔ List<String>으로 변환
        List<String> keepImageUrls = new ObjectMapper().readValue(keepImageUrlsJson, new TypeReference<List<String>>(){});

        Post updatedPost = objectMapper.readValue(updatedPostJson, Post.class);

        return postService.updatePostWithImages(postId, username, newImageFiles, keepImageUrls, updatedPost);
    }

    /**
     * 글 삭제 (작성자만 가능, S3 사진도 삭제)
     */
    @DeleteMapping("/{postId}")
    public boolean deletePost(@PathVariable String postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return postService.deletePost(postId, username);
    }
}


