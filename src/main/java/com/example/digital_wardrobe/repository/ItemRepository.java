package com.example.digital_wardrobe.repository;

import com.example.digital_wardrobe.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findByWardrobeId(String wardrobeId);
    
    List<Item> findByUsername(String username);
}


