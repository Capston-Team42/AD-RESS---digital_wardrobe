package com.example.digital_wardrobe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.example.digital_wardrobe.repository.team",
        mongoTemplateRef = "teamMongoTemplate"
)
public class TeamMongoConfig {
}

