package com.example.digital_wardrobe.repository.team;

import com.example.digital_wardrobe.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    
    // 🔹 Username 검색 (부분일치, 대소문자 무시)
    @Query("{ 'username': { $regex: ?0, $options: 'i' } }")
    Page<User> searchUsers(String keyword, Pageable pageable);


}
