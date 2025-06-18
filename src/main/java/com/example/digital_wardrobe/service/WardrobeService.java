package com.example.digital_wardrobe.service;

import com.example.digital_wardrobe.model.Wardrobe;
import com.example.digital_wardrobe.repository.WardrobeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class WardrobeService {

    private final WardrobeRepository wardrobeRepository;

    public WardrobeService(WardrobeRepository wardrobeRepository) {
        this.wardrobeRepository = wardrobeRepository;
    }

    public List<Wardrobe> getWardrobesByUsername(String username) {
        return wardrobeRepository.findByUsername(username);
    }

    public Wardrobe createWardrobe(Wardrobe wardrobe) {
        return wardrobeRepository.save(wardrobe);
    }

    public void deleteWardrobe(String id, String username) {
        Optional<Wardrobe> optionalWardrobe = wardrobeRepository.findById(id);

        optionalWardrobe.ifPresent(wardrobe -> {
            if (!wardrobe.getUsername().equals(username)) {
                throw new RuntimeException("삭제 권한이 없습니다.");
            }
            wardrobeRepository.deleteById(id);
        });
    }
}
