package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.service.LikePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikePostController {

    private final LikePostService likePostService;

    /**
     * ❤️ 좋아요 상태 토글 (프론트에서 하트 클릭 시)
     * @return true → 좋아요 누름, false → 좋아요 취소
     */
    @PostMapping("/toggle")
    public Map<String, Object> toggleLike(@RequestBody Map<String, String> request) {
        String postId = request.get("postId");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return likePostService.toggleLike(postId, username);  // ✅ 토큰 기반 username 사용
    }

    /**
     * ✅ 특정 유저가 이 글에 좋아요 눌렀는지 확인
     */
    @GetMapping("/check")
    public boolean isLiked(@RequestParam String postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return likePostService.isLiked(postId, username);
    }

    /**
     * 📊 해당 글의 좋아요 수 조회
     */
    @GetMapping("/count")
    public long getLikeCount(@RequestParam String postId) {
        return likePostService.getLikeCount(postId);
    }
}


