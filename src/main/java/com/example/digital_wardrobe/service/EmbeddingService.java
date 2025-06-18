package com.example.digital_wardrobe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${openai.api.key}")
    private String apiKey;
    private final String url = "https://api.openai.com/v1/embeddings";
    
    public List<Double> generateEmbedding(List<String> tags) {
        String input = String.join(" ", tags);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("input", input);
        body.put("model", "text-embedding-3-small");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            System.out.println("📦 GPT embedding API 응답:");
            System.out.println(response.getBody());

            List<Double> embedding = (List<Double>) ((Map) ((List<?>) response.getBody().get("data")).get(0)).get("embedding");
            return embedding;

        } catch (Exception e) {
            System.out.println("❌ 임베딩 API 호출 실패:");
            e.printStackTrace();
            return null; // 또는 Collections.emptyList(); 로 해도 됨
        }
    }


}
