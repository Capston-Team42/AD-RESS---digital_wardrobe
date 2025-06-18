package com.example.digital_wardrobe.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {
            "com.example.digital_wardrobe.repository"  // ✅ 내 repository만
        },
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.repository\\.team\\..*"),
        mongoTemplateRef = "mongoTemplate"
)
public class PrimaryMongoConfig {
}
