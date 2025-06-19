# 디지털 옷장 및 커뮤니티 (백엔드)

해당 리포지토리는 AD*RESS의 디지털 옷장과 커뮤니티 기능의 백엔드 코드를 포함하고 있습니다.
디지털 옷장 기능을 통해 사용자는 옷장을 생성 및 관리할 수 있고 옷장에 옷 이미지를 업로드하여 태그 분석 결과와 함께 옷장에 옷을 저장할 수 있습니다. <br> 커뮤니티 기능을 통해 패션에 관한 게시글을 작성할 수 있으며 댓글과 좋아요 기능을 통해 다른 사용자와 소통할 수 있습니다.

---

## 📁 Source Code 설명

본 프로젝트의 주요 구조는 다음과 같습니다:

```
digitalWardrobe/
├── src/
│   └── main/
│       ├── java/com/example/digital_wardrobe/
│       │   ├── config/              # MongoDB, JWT, CORS, Security 설정
│       │   ├── controller/          # API 엔드포인트: 아이템 CRUD, 옷장CRUD, 게시글, 댓글, 좋아요 등
│       │   ├── dto/                 # 요청/응답용 DTO 클래스들
│       │   ├── model/               # MongoDB 도메인 모델 (Item, Post, User, Wardrobe 등)
│       │   ├── repository/          # MongoRepository 인터페이스
│       │   └── DigitalWardrobeApplication.java  # 메인 실행 클래스
│       └── resources/
│           └── application.properties  # DB 연결 정보, api key 등 환경 설정
├── .gitignore
├── pom.xml

```

---

## 🛠️ How to Build

### 1️⃣ GitHub 저장소 클론

먼저 프로젝트 코드를 로컬로 복사해옵니다:

```bash
git clone https://github.com/Capston-Team42/AD-RESS---digital_wardrobe.git
cd AD-RESS---digital_wardrobe
```

### 2️⃣ 환경설정 `application.properties` 파일 추가
- `src/main/` 경로에 resources 폴더를 직접 생성해주세요.
- `src/main/resources/` 경로에 `application.properties` 파일을 직접 넣어주세요.
- 이 파일에는 MongoDB 접속 정보, open ai와 aws s3의 api key 그리고 JWT 시크릿 키 등이 포함되어 있습니다.
- **보안을 위해 해당 파일은 GitHub에 포함되어 있지 않으며, 따로 메일로 전달드렸습니다.**

### 📦 빌드

모든 준비가 완료되면 아래 명령어로 프로젝트를 빌드하세요:

```bash
./mvnw clean package
```

- 이 명령은 Maven Wrapper를 사용하여 의존성(Jar 파일들)을 자동으로 설치하고,
- `target/` 폴더 안에 `.jar` 실행 파일을 생성합니다.

> 💡 윈도우 사용자는 `./mvnw` 대신 `mvnw.cmd` 사용:

```bash
mvnw.cmd clean package
```

---
## 🚀 How to Install & Run

빌드 후 실행:

```bash
java -jar target/digital_wardrobe-0.0.1-SNAPSHOT.jar
```

---
## 📮 How to Test

### ✅ Postman을 이용한 API 테스트

본 프로젝트는 JWT 기반 인증이 적용되어 있으며, 모든 API 요청 시 **`Authorization` 헤더에 토큰을 포함**해야 합니다.

> 토큰 발급은 로그인 API를 통해 획득하며, 이후 모든 요청에 아래와 같이 헤더를 설정합니다:
> ```
> Authorization: Bearer [발급받은 토큰]
> ```

### 🧪 API 테스트 목록 (Postman에서 사용 가능)

#### 1. 옷장 목록 조회
- **GET** `http://localhost:8081/api/wardrobes/me`
- 헤더: `Authorization: Bearer [토큰]`

#### 2. 옷장 생성
- **POST** `http://localhost:8081/api/wardrobes`
- 헤더:
  - `Authorization: Bearer [토큰]`
  - `Content-Type: application/json`
- Body (JSON):
  ```json
  {
    "wardrobeName": "봄 옷장"
  }
  ```

#### 3. 옷장 삭제
- **DELETE** `http://localhost:8081/api/wardrobes/{wardrobeId}`
- 헤더: `Authorization: Bearer [토큰]`

##### 4. 아이템 목록 조회 (특정 옷장)
- **GET** `http://localhost:8081/api/items/wardrobe/{wardrobeId}`
- 헤더: `Authorization: Bearer [토큰]`

##### 5. 아이템 목록 조회 (자신의 모든 아이템)
- **GET** `http://localhost:8081/api/items/me`
- 헤더: `Authorization: Bearer [토큰]`

##### 6. 아이템 분석 (이미지 → 태그 추출)
- **POST** `http://localhost:8081/api/items/analyze`
- 헤더:
  - `Authorization: Bearer [토큰]`
  - `Content-Type: multipart/form-data`
- Body (form-data):
  - `file`: 이미지 파일

##### 7. 아이템 등록 (이미지 + 태그 저장)
- **POST** `http://localhost:8081/api/items/create`
- 헤더:
  - `Authorization: Bearer [토큰]`
  - `Content-Type: application/json`
- Body (JSON):
  - `아이템 분석 결과 + wardrobeId`
  - 예시:
    ```json
    {
      "wardrobeId": "abc123",
      "color": "ivory",
      "fit": ["loose"],
      "material": "cotton",
      "tpo": "date",
      "style": "lovely"
    }
    ```

##### 8. 아이템 수정 (태그만 수정 가능, 이미지 수정 불가)
- **PUT** `http://localhost:8081/api/items/update/{itemId}`
- 헤더:
  - `Authorization: Bearer [토큰]`
  - `Content-Type: application/json`
- Body (JSON): 변경할 속성만 포함
  ```json
  {
    "color": "beige"
  }
  ```

##### 9. 아이템 삭제
- **DELETE** `http://localhost:8081/api/items/delete/{itemId}`
- 헤더: `Authorization: Bearer [토큰]`

### 🔧 테스트 도구

- **Postman**에서 요청 헤더, 바디, 파라미터 설정을 통해 위 API들을 쉽게 테스트할 수 있습니다.
- 서버는 `http://localhost:8081`에서 실행되며, 로컬에서 직접 Spring Boot를 실행한 후 테스트합니다.

---
## 📦 사용한 Open Source 및 외부 라이브러리

`pom.xml`에 명시된 주요 라이브러리 및 외부 서비스는 다음과 같습니다:

- `spring-boot-starter-web`: 웹 애플리케이션 및 REST API 구성
- `spring-boot-starter-security`: Spring Security 기반 인증 및 인가
- `spring-boot-starter-data-mongodb`: MongoDB 연동
- `spring-boot-devtools`: 개발용 핫리로드 지원
- `spring-boot-starter-test`: 테스트 코드 작성용 종합 의존성
- `springdoc-openapi-starter-webmvc-ui`: Swagger UI를 통한 API 문서 자동화
- `lombok`: 반복 코드 생략을 위한 어노테이션 지원
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`: JWT 토큰 생성 및 검증
- `software.amazon.awssdk:s3`: AWS S3와 이미지 업로드/삭제 연동
- `com.fasterxml.jackson.databind`: JSON 직렬화/역직렬화 (ObjectMapper 사용)

---

### 🔗 외부 서비스 연동

- **OpenAI GPT API**:  
  옷 이미지 분석을 통해 태그를 자동 생성하는 데 사용되었습니다. REST API 방식으로 호출되며, `pom.xml`에는 명시되어 있지 않지만 기능상 중요한 역할을 합니다.

