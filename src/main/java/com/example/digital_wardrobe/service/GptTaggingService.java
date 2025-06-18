package com.example.digital_wardrobe.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class GptTaggingService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String apiKey;

    
    public Map<String, Object> extractTagsFromImage(String image_url) {
    	String prompt = """
    			You are a fashion tagging AI. Analyze the clothing item in the image and return the tags in the following JSON format.
    			Respond only with **RAW JSON**. **Start with '{' and end with '}'**. **Do not include ```json or ```**.

    			Return a **flat JSON object**.
    			Include "imageUrl": "<input_image_url>" as the first field.
    			Then, determine the "type" of the clothing item:
    			One of the following: "outer", "top", "dress", "pants", "skirt"
    			
    			Then, based on the type, return the corresponding tags as **top-level keys**, not under a "tags" field.

    			1️. Common Fields (all types):
    			- "style1": one of ["casual", "street", "gorpcore", "workwear", "preppy", "sporty", "romantic", "girlish", "classic", "minimal", "chic", "retro", "ethnic", "resort", "balletcore"]
    			- "style2": (same choices as style1)
    			- "style3": (same choices as style1)
    			- "color": one of ["red", "orange", "yellow", "light green", "green", "blue green", "blue", "navy", "purple", "red purple", "achromatic", "pink"]
    			- "saturation": one of ["high", "medium", "low", "achromatic"]
    			- "brightness": one of ["white", "high", "medium", "low", "black"]
    			- "pattern": one of ["stripe", "check", "flower", "dot", "patchwork", "camouflage", "paisley", "tropical", "hound tooth", "herringbone", "other pattern", "plain"]
    			- "season": array of ["spring", "summer", "autumn", "winter"]
    			- "tpo": free text (English)
    			- "detail1": free text (English)
    			- "detail2": free text (English)
    			- "detail3": free text (English)

    			2️. Type-Specific Fields:
    			- For "outer":
    			  - "category": one of ["hooded zip-up”, “blouson/MA-1”, “leather/riders jacket”, “cardigan”, “trucker jacket”, “suit/blazer jacket”, “stadium jacket”, “nylon/coach jacket”, “anorak jacket”, “training jacket”, "season change coat", “safari/hunting jacket”, “padding”, “mustang/fur”, “fleece”, “winter coat”, “tweed jacket”]
    			  - "topLength": one of ["half", "crop", "regular", "long"]
    			  - "sleeveLength": one of ["sleeveless", "short sleeves", "three-quarter sleeves", "long sleeves"]
    			  - "fit": one of ["slim", "regular", "oversize"]
    			  - "isSeeThrough": true or false

    			- For "top":
    			  - "category": one of ["sweatshirt”, “hooded sweatshirt”, “shirt/blouse”, “t-shirt”, “knit”]
    			  - "topLength": one of ["half", "crop", "regular", "long"]
    			  - "sleeveLength": one of ["sleeveless", "short sleeves", "three-quarter sleeves", "long sleeves"]
    			  - "fit": one of ["slim", "regular", "oversize"]
    			  - "isSimple": true or false
    			  - "isSeeThrough": true or false
    			  - "print": free text (English)

    			- For "dress":
    			  - "skirtLength": one of ["mini skirt", "midi skirt", "long skirt"]
    			  - "skirtFit": one of ["a-line", "h-line", "balloon", "pencil"]
    			  - "skirtType": one of ["pleats", "wrap", "tiered", "skirt pants", "cancan", "plain"]
    			  - "sleeveLength": one of ["sleeveless", "short sleeves", "three-quarter sleeves", "long sleeves"]
    			  - "fit": one of ["slim", "regular", "oversize"]
    			  - "isTopRequired": true or false
    			  - "isSeeThrough": true or false

    			- For "pants":
    			  - "category": one of [“denim pants”, “training pants”, “cotton pants”, “suit pants/slack”, “leggings”, “jumpsuit/overall”]
    			  - "bottomLength": one of ["shorts", "bermuda pants", "capri pants", "ankle pants", "long pants"]
    			  - "pantsFit": one of ["wide", "straight", "tapered", "slim/skinny", "boot cut", "baggy fit", "jogger fit"]

    			- For "skirt":
    			  - "skirtLength": one of ["mini skirt", "midi skirt", "long skirt"]
    			  - "skirtFit": one of ["a-line", "h-line", "balloon", "pencil"]
    			  - "skirtType": one of ["pleats", "wrap", "tiered", "skirt pants", "cancan", "plain"]

    			Respond with valid JSON.
    			Use double quotes for all keys and values.
    			image_url: %s
    			""".formatted(image_url);



        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o");
        List<Map<String, Object>> contentList = List.of(
                Map.of("type", "text", "text", prompt),
                Map.of("type", "image_url", "image_url", Map.of("url", image_url))
            );

            Map<String, Object> message = Map.of(
                "role", "user",
                "content", contentList
            );

            body.put("messages", List.of(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", request, Map.class
        );

        try {
            Map<String, Object> choices = (Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0);
            String content = (String) ((Map<?, ?>) choices.get("message")).get("content");

            // ✅ GPT 응답 원문 출력
            System.out.println("🧠 GPT 응답 원문:");
            System.out.println(content);

            // JSON 응답 문자열 → Map으로 파싱
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, Map.class);

        } catch (Exception e) {
        	// ✅ 파싱 실패 시에도 응답 출력
            System.out.println("❌ GPT 응답 파싱 실패! 응답 원문:");
            System.out.println(response);
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage());

        }
    }
}
