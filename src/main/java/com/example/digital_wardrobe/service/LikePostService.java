package com.example.digital_wardrobe.service;

import com.example.digital_wardrobe.model.LikePost;
import com.example.digital_wardrobe.model.Post;
import com.example.digital_wardrobe.repository.LikePostRepository;
import com.example.digital_wardrobe.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikePostService {

    private final LikePostRepository likePostRepository;
    private final PostRepository postRepository;

    // ❤️ 좋아요 누르기
    public boolean likePost(String postId, String userId) {
        if (likePostRepository.existsByPostIdAndUsername(postId, userId)) {
            return false; // 이미 눌렀음
        }

        likePostRepository.save(
        	    LikePost.builder()
        	        .postId(postId)
        	        .username(userId)
        	        .createdAt(LocalDateTime.now())
        	        .build()
        	);


        postRepository.findById(postId).ifPresent(post -> {
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
        });

        return true;
    }

    // 🤍 좋아요 취소
    public boolean unlikePost(String postId, String userId) {
        Optional<LikePost> like = likePostRepository.findByPostIdAndUsername(postId, userId);
        if (like.isEmpty()) return false;

        likePostRepository.deleteByPostIdAndUsername(postId, userId);

        postRepository.findById(postId).ifPresent(post -> {
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
        });

        return true;
    }
    
    public Map<String, Object> toggleLike(String postId, String userId) {
        boolean liked;

        if (likePostRepository.existsByPostIdAndUsername(postId, userId)) {
            unlikePost(postId, userId);
            liked = false;
        } else {
            likePost(postId, userId);
            liked = true;
        }

        long likeCount = getLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return response;
    }
    
    // 🔍 특정 유저가 이 글에 좋아요 눌렀는지
    public boolean isLiked(String postId, String userId) {
        return likePostRepository.existsByPostIdAndUsername(postId, userId);
    }

    // 📊 좋아요 총 개수 조회
    public long getLikeCount(String postId) {
        return likePostRepository.countByPostId(postId);
    }
}
