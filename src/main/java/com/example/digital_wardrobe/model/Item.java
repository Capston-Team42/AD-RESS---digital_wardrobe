package com.example.digital_wardrobe.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "UserItem")
public class Item {

    @Id
    private String id;
    private String username;
    private String wardrobeId;
    private String imageUrl;

    // 🔹 Style 1~3 
    private String style1;
    private String style2;
    private String style3;

    // 🔹 공통 속성 (일반 검색)
    private String color;
    private String saturation;
    private String brightness;
    private String pattern;
    private List<String> season;

    // 🔹 TPO (벡터 검색)
    private String tpo;
    private List<Double> tpoEmbedding;

    // 🔹 Detail 1~3 (각각 필드 + embedding 따로)
    private String detail1;
    private List<Double> detail1Embedding;

    private String detail2;
    private List<Double> detail2Embedding;

    private String detail3;
    private List<Double> detail3Embedding;

    // 🔹 공통 Boolean 속성
    private Boolean isSeeThrough;
    private Boolean isSimple;
    private Boolean isTopRequired;
    private Boolean isUnique;

    // 🔹 타입 구분 필드 (top, pants, skirt, dress, outer)
    private String type;

    // 🔹 카테고리 (type별 값)
    private String category;

    // 🔹 Top, Outerwear 공통 속성
    private String topLength;
    private String sleeveLength;
    private String fit;
    private String print;
    private List<Double> printEmbedding;

    // 🔹 Pants 전용 속성
    private String bottomLength;
    private String pantsFit;

    // 🔹 Skirt, Dress 전용 속성
    private String skirtLength;
    private String skirtFit;
    private String skirtType;
}
