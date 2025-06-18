package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.model.Item;
import com.example.digital_wardrobe.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ GPT 태그 분석
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = itemService.analyzeImage(file);
        return ResponseEntity.ok(result);
    }

    // ✅ 아이템 등록 (username 자동 세팅)
    @PostMapping("/create")
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        item.setUsername(username);
        Item createdItem = itemService.createItem(item);
        return ResponseEntity.ok(createdItem);
    }

    // ✅ 아이템 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable String id,
            @RequestBody Item updatedItem) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return itemService.updateItem(id, updatedItem, username)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // ✅ 내 아이템 목록 조회
    @GetMapping("/me")
    public ResponseEntity<List<Item>> getMyItems() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(itemService.getItemsByUsername(username));
    }

    // ✅ 옷장별 아이템 조회
    @GetMapping("/wardrobe/{wardrobeId}")
    public ResponseEntity<List<Item>> getItemsByWardrobe(@PathVariable String wardrobeId) {
        return ResponseEntity.ok(itemService.getItemsByWardrobeId(wardrobeId));
    }

    // ✅ 아이템 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        itemService.deleteItem(id, username);
        return ResponseEntity.ok().build();
    }
}
