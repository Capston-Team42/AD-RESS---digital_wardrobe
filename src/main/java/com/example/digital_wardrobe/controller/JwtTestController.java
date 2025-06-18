package com.example.digital_wardrobe.controller;

import com.example.digital_wardrobe.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-jwt")
@RequiredArgsConstructor
public class JwtTestController {

    private final JwtUtils jwtUtils;

    @GetMapping("/generate")
    public String generateToken(@RequestParam String username) {
        return jwtUtils.generateToken(username);
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam String token) {
        boolean valid = jwtUtils.validateToken(token);
        if (valid) {
            return "✅ Token Valid. Username: " + jwtUtils.getUsername(token);
        } else {
            return "❌ Invalid Token.";
        }
    }
}
