package com.example.digital_wardrobe.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String primaryUri;

    @Value("${spring.data.mongodb.database}")
    private String primaryDatabase;

    @Value("${team.data.mongodb.uri}")
    private String teamUri;

    @Value("${team.data.mongodb.database}")
    private String teamDatabase;

    @Primary
    @Bean(name = "mongoTemplate")  // ✅ 기본 mongoTemplate (내 DB)
    public MongoTemplate primaryMongoTemplate() {
        MongoClient mongoClient = createClientWithLocalReadConcern(primaryUri);
        return new MongoTemplate(mongoClient, primaryDatabase);
    }

    @Bean(name = "teamMongoTemplate")  // ✅ teamMongoTemplate (팀원 DB)
    public MongoTemplate teamMongoTemplate() {
        MongoClient mongoClient = createClientWithLocalReadConcern(teamUri);
        return new MongoTemplate(mongoClient, teamDatabase);
    }

    // ✅ 공통 메서드: ReadConcern.LOCAL 적용된 클라이언트 생성
    private MongoClient createClientWithLocalReadConcern(String uri) {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .readConcern(ReadConcern.LOCAL)  // ❗ 이 줄이 핵심!
                .build();
        return MongoClients.create(settings);
    }
}

