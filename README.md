# AD-RESS: 디지털 옷장 백엔드 프로젝트

**AD-RESS**는 Spring Boot 기반의 디지털 옷장 관리 및 커뮤니티 서비스입니다.  
사용자는 옷장을 생성 및 관리할 수 있고 옷장에 옷 이미지를 업로드하여 태그 분석 결과와 함께 옷을 저장할 수 있습니다. <br> 커뮤니티를 통해 옷 코디에 대한 피드백을 주고받을 수 있습니다.

---

## 📁 Source Code 설명

본 프로젝트의 주요 구조는 다음과 같습니다:

```
digitalWardrobe/
├── src/
│   └── main/
│       ├── java/com/example/digital_wardrobe/
│       │   ├── config/              # MongoDB, JWT, CORS, Security 설정
│       │   ├── controller/          # API 엔드포인트: 아이템, 옷장, 게시글, 댓글, 검색 등
│       │   ├── dto/                 # 요청/응답용 DTO 클래스들
│       │   ├── model/               # MongoDB 도메인 모델 (Item, Post, User 등)
│       │   ├── repository/          # MongoRepository 인터페이스
│       │   └── DigitalWardrobeApplication.java  # 메인 실행 클래스
│       └── resources/
│           └── application.properties  # DB 연결 정보 등 환경 설정
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🛠️ How to Build

- **JDK 버전**: 21
- **Build Tool**: Maven

빌드 방법:

```bash
./mvnw clean package
```

---

## 🚀 How to Install & Run

빌드 후 실행:

```bash
java -jar target/digital_wardrobe-0.0.1-SNAPSHOT.jar
```

---

## 🧪 How to Test

 Postman 등을 이용한 REST API 테스트 가능


---

## 🗃️ Database or Data Used

- **MongoDB Atlas** 사용
- 연결 정보는 `application.properties`에 설정되어 있으며, 보안을 위해 해당 파일은 Git에 포함되어 있지 않습니다.
- 평가 목적의 실행을 위해 관련 내용을 교수님께 별도 메일로 전달드렸습니다.

---

## 📦 사용한 Open Source 라이브러리

`pom.xml`에 명시된 주요 라이브러리는 다음과 같습니다:

- `spring-boot-starter-web`: 웹 애플리케이션 및 REST API 구성
- `spring-boot-starter-security`: Spring Security 기반 인증 및 인가
- `spring-boot-starter-data-mongodb`: MongoDB 연동
- `jjwt`: JWT 토큰 생성 및 검증
- `lombok`: 반복 코드 생략을 위한 어노테이션 지원
- `spring-boot-maven-plugin`: Maven 빌드 지원

---
