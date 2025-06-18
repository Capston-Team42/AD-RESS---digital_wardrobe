package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.model.Post;
import com.example.digital_wardrobe.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    // 🔍 통합 검색: username / content / hashtags 중 하나라도 키워드 포함 시 결과 반환
    @GetMapping("/posts")
    public Page<Post> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return searchService.searchPosts(keyword, page, size);
    }
}


