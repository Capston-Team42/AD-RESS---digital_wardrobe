package com.example.digital_wardrobe.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserWardrobe")
public class Wardrobe {
    @Id
    private String id;
    private String username;
    private String wardrobeName;

    public Wardrobe() {}

    public Wardrobe(String username, String wardrobeName) {
        this.username = username;
        this.wardrobeName = wardrobeName;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String userId) {
        this.username = userId;
    }
    public String getWardrobeName() {
        return wardrobeName;
    }
    public void setWardrobeName(String wardrobeName) {
        this.wardrobeName = wardrobeName;
    }
}

