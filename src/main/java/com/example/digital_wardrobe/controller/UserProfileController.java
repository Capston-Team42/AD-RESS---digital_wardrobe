package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.dto.MyProfileDto;
import com.example.digital_wardrobe.dto.UserProfileDto;
import com.example.digital_wardrobe.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // ✅ 내 프로필 조회 (JWT 인증 기반)
    @GetMapping("/me")
    public MyProfileDto getMyProfile(Authentication authentication) {
        String username = authentication.getName();  // JWT에서 추출된 username
        return userProfileService.getMyProfile(username);
    }

    // ✅ 다른 사용자 프로필 조회
    @GetMapping("/{username}")
    public UserProfileDto getOtherUserProfile(@PathVariable String username) {
        return userProfileService.getOtherUserProfile(username);
    }

}

