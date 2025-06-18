package com.example.digital_wardrobe.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "roles")
public class Role {

    @Id
    private String id;

    private String name;  // ex) ROLE_USER, ROLE_ADMIN
}

