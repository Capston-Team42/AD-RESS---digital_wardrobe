package com.example.digital_wardrobe.service;

import com.example.digital_wardrobe.model.Item;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import java.beans.PropertyDescriptor;

import com.example.digital_wardrobe.repository.ItemRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final EmbeddingService embeddingService;
    private final GptTaggingService gptService;
    
    private final S3Service s3Service;

    public ItemService(ItemRepository itemRepository, EmbeddingService embeddingService, S3Service s3Service, GptTaggingService gptService) {
        this.itemRepository = itemRepository;
        this.embeddingService = embeddingService;
        this.s3Service = s3Service;
        this.gptService = gptService;
    }

    public List<Item> getItemsByUsername(String username) {
        return itemRepository.findByUsername(username);
    }

    public List<Item> getItemsByWardrobeId(String wardrobeId) {
        return itemRepository.findByWardrobeId(wardrobeId);
    }
    public Map<String, Object> analyzeImage(MultipartFile multipartFile) {
        try {
            // 🔹 MultipartFile → File 변환
            File file = s3Service.convertMultiPartToFile(multipartFile);

            // 🔹 S3 업로드
            String imageUrl = s3Service.uploadFile(file);

            // 🔹 임시 파일 삭제
            file.delete();

            // 🔹 GPT API 호출하여 태그 추출
            Map<String, Object> tagResult = gptService.extractTagsFromImage(imageUrl);  // 👉 너가 만든 GPT 호출 메서드 연결 필요

            return tagResult;  // 완전 깔끔하게 flat 구조 유지됨

        } catch (IOException e) {
            throw new RuntimeException("analyzeImage 실패: " + e.getMessage());
        }
    }


    public Item createItem(Item item) {
        // 🔹 임베딩 생성이 필요한 필드만 명시 (tpo, detail1, detail2, detail3)
        List<String> embeddingFields = List.of("tpo", "detail1", "detail2", "detail3");

        for (String tag : embeddingFields) {
            try {
                // 🔹 각 필드의 getter 메서드 이름 생성 (ex: getTpo)
                String getterName = "get" + tag.substring(0, 1).toUpperCase() + tag.substring(1);
                Method getter = Item.class.getMethod(getterName);
                Object value = getter.invoke(item); // 현재 item 객체에서 값 가져오기

                if (value != null) {
                    List<Double> embedding;

                    // 🔹 String 타입 필드 처리
                    if (value instanceof String strVal) {
                        embedding = embeddingService.generateEmbedding(List.of(strVal));
                    }
                    // 🔹 List<String> 타입 필드 처리
                    else if (value instanceof List listVal) {
                        embedding = embeddingService.generateEmbedding((List<String>) listVal);
                    } else {
                        continue; // 해당 타입이 아니면 skip
                    }

                    // 🔹 해당 필드의 embedding setter 이름 생성 (ex: setTpo_embedding)
                    String setterName = "set" + tag.substring(0, 1).toUpperCase() + tag.substring(1) + "Embedding";
                    Method setter = Item.class.getMethod(setterName, List.class);

                    // 🔹 임베딩 값 주입
                    setter.invoke(item, embedding);
                }
            } catch (Exception e) {
                e.printStackTrace(); // 예외는 로그만 찍고 무시
            }
        }

        // 🔹 최종적으로 DB 저장
        return itemRepository.save(item);
    }

    public void deleteItem(String id, String requesterUsername) {
        itemRepository.findById(id).ifPresent(item -> {
            if (!item.getUsername().equals(requesterUsername)) {
                throw new RuntimeException("삭제 권한이 없습니다.");
            }

            if (item.getImageUrl() != null) {
                s3Service.deleteFile(item.getImageUrl());
            }

            itemRepository.deleteById(id);
        });
    }
    
    private void copyNonNullProperties(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper trg = new BeanWrapperImpl(target);

        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String propertyName = pd.getName();
            Object srcValue = src.getPropertyValue(propertyName);

            if (srcValue != null && trg.isWritableProperty(propertyName)) {
                trg.setPropertyValue(propertyName, srcValue);
            }
        }
    }

    
    public Optional<Item> updateItem(String id, Item updatedItem, String requesterUsername) {
        return itemRepository.findById(id).map(item -> {

            // 🔑 1. 권한 체크
            if (!item.getUsername().equals(requesterUsername)) {
                throw new RuntimeException("수정 권한이 없습니다.");
            }

            // 🔑 2. 임베딩 필드 업데이트 (tpo, detail1~3)
            List<String> embeddingFields = List.of("tpo", "detail1", "detail2", "detail3");

            for (String tag : embeddingFields) {
                try {
                    String getterName = "get" + tag.substring(0, 1).toUpperCase() + tag.substring(1);
                    Method getter = Item.class.getMethod(getterName);
                    Object value = getter.invoke(updatedItem);

                    if (value != null) {
                        List<Double> embedding;

                        if (value instanceof String strVal) {
                            embedding = embeddingService.generateEmbedding(List.of(strVal));
                        } else if (value instanceof List listVal) {
                            embedding = embeddingService.generateEmbedding((List<String>) listVal);
                        } else {
                            continue;
                        }

                        String setterName = "set" + tag.substring(0, 1).toUpperCase() + tag.substring(1) + "Embedding";
                        Method setter = Item.class.getMethod(setterName, List.class);

                        setter.invoke(item, embedding);

                        // 값 자체도 업데이트
                        Method itemSetter = Item.class.getMethod("set" + tag.substring(0, 1).toUpperCase() + tag.substring(1), value.getClass());
                        itemSetter.invoke(item, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 🔑 3. 일반 필드는 null 아닌 값만 덮어쓰기
            copyNonNullProperties(updatedItem, item);

            // 🔑 4. 저장
            return itemRepository.save(item);
        });
    }



}