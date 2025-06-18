package com.example.digital_wardrobe.repository;

import com.example.digital_wardrobe.model.Wardrobe;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WardrobeRepository extends MongoRepository<Wardrobe, String> {
    List<Wardrobe> findByUsername(String userId);
}

