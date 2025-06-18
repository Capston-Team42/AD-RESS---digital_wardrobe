package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.model.Wardrobe;
import com.example.digital_wardrobe.service.WardrobeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.VariableOperators.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/wardrobes")
public class WardrobeController {

    private final WardrobeService wardrobeService;

    public WardrobeController(WardrobeService wardrobeService) {
        this.wardrobeService = wardrobeService;
    }

    // 🔹 내 옷장 목록 조회 (Token 기반)
    @GetMapping("/me")
    public ResponseEntity<List<Wardrobe>> getMyWardrobes() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Wardrobe> wardrobes = wardrobeService.getWardrobesByUsername(username);
        return ResponseEntity.ok(wardrobes);
    }

    // 🔹 옷장 생성 (Token 기반)
    @PostMapping
    public ResponseEntity<Wardrobe> createWardrobe(@RequestBody Wardrobe wardrobe) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        wardrobe.setUsername(username);
        Wardrobe saved = wardrobeService.createWardrobe(wardrobe);
        return ResponseEntity.ok(saved);
    }

    // 🔹 옷장 삭제 (username 인증 체크는 service 내부에서)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWardrobe(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        wardrobeService.deleteWardrobe(id, username);
        return ResponseEntity.ok("Wardrobe deleted");
    }
}
