package com.example.digital_wardrobe.service;

import com.example.digital_wardrobe.model.Post;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MongoTemplate mongoTemplate;

    public Page<Post> searchPosts(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }

        Document searchStage = new Document("$search",
            new Document("index", "default")
                .append("compound", new Document("should", List.of(
                    createTextQuery("username", keyword),
                    createTextQuery("content", keyword),
                    createTextQuery("hashtags", keyword)
                )))
        );

        int skip = page * size;

        Aggregation aggregation = Aggregation.newAggregation(
            context -> searchStage,
            Aggregation.skip(skip),
            Aggregation.limit(size)
        );

        AggregationResults<Post> results = mongoTemplate.aggregate(aggregation, "posts", Post.class);
        List<Post> matchedPosts = results.getMappedResults();
        long total = countSearchResults(keyword);

        return new PageImpl<>(matchedPosts, PageRequest.of(page, size), total);
    }

    private Document createTextQuery(String field, String keyword) {
        return new Document("text", new Document("query", keyword).append("path", field));
    }

    private long countSearchResults(String keyword) {
        Document countStage = new Document("$search",
            new Document("index", "default")
                .append("compound", new Document("should", List.of(
                    createTextQuery("username", keyword),
                    createTextQuery("content", keyword),
                    createTextQuery("hashtags", keyword)
                )))
        );

        Aggregation aggregation = Aggregation.newAggregation(
            context -> countStage,
            Aggregation.count().as("total")
        );

        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, "posts", Document.class);
        if (!result.getMappedResults().isEmpty()) {
            return result.getMappedResults().get(0).getInteger("total");
        }
        return 0;
    }
}




