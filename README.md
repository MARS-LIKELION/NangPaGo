# NangPaGo

![alt text](assets/thumbnail.png)

## 👋 소개

냉장고 속 남은 재료로 무엇을 요리할지 고민되시나요?  
**냉파고**는 냉장고 속 재료를 활용할 수 있는 레시피를 추천해주는 웹 애플리케이션입니다.

### 🛠️ 사용 기술

**Backend**

![Java](https://img.shields.io/badge/☕_Java-F89820?&style=for-the-badge&logo=Java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?&style=for-the-badge&logo=SpringBoot&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?&style=for-the-badge&logo=JUnit5&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?&style=for-the-badge&logo=Gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?&style=for-the-badge&logo=Swagger&logoColor=black)

**Database & Storage**

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Firebase Storage](https://img.shields.io/badge/Firebase_Storage-DD2C00?style=for-the-badge&logo=firebase&logoColor=white)

**Search Engine & Message Broker**

![ElasticSearch](https://img.shields.io/badge/ElasticSearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

**DevOps & Infrastructure**

![macOS](https://img.shields.io/badge/macOS(server)-000000?style=for-the-badge&logo=macos&logoColor=white)
![NGINX](https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=nginx&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)

**Frontend**

![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Redux](https://img.shields.io/badge/Redux-764ABC?style=for-the-badge&logo=redux&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)

## 🕒 프로젝트 기간

2024.12.17 ~ 2025.02.13 **(2개월)** 


## 🎯 프로젝트 목적

- ElasticSearch 기반 자연어 검색 및 자동완성 기능 구현
- 직관적인 UI/UX를 통한 Seamless User Experience 제공
- RabbitMQ를 활용한 비동기 처리로 시스템 확장성 및 성능 향상
- SSE(Server-Sent Events) 기반 실시간 알림 시스템 구현
- GitHub Actions와 Jenkins를 활용한 CI/CD 파이프라인 구축으로 안정적인 배포 환경 조성
- 관리자 대시보드를 통한 사용자 활동 모니터링 및 서비스 분석 기능 제공


## 📈 기대효과

**사용자 편의성 증대**  
- ElasticSearch 기반 자연어 검색으로 오타, 초성/중성 분리를 통한 정확한 검색 결과 제공
- SSE를 활용한 실시간 알림으로 즉각적인 사용자 피드백 제공
- OAuth 2.0 기반 소셜 로그인으로 간편한 사용자 인증 지원
- Firebase Storage를 활용한 이미지 최적화로 빠른 콘텐츠 로딩 제공

**시스템 안정성 및 성능 향상**  
- RabbitMQ를 활용한 비동기 처리로 시스템 부하 분산 및 확장성 확보
- Blue-Green 배포 전략으로 무중단 서비스 제공
- Docker 컨테이너화를 통한 일관된 운영 환경 구성
- 분산 데이터베이스 구조(MySQL, MongoDB)로 데이터 처리 효율성 향상

**운영 효율성 개선**  
- GitHub Actions와 Jenkins를 활용한 CI/CD 파이프라인으로 배포 자동화
- Docker 기반 마이크로서비스 아키텍처로 서비스 독립성 확보
- 관리자 대시보드를 통한 통합 모니터링 환경 제공
- 체계적인 로깅 시스템으로 문제 상황 추적 용이

**데이터 관리 최적화**  
- ElasticSearch를 활용한 효율적인 데이터 검색 및 분석
- 정규화된 레시피 데이터 처리로 높은 데이터 품질 확보
- Firebase Storage의 이미지 최적화로 스토리지 비용 절감

---

## 📋 주요 기능

### 🔑 사용자 인증
- **OAuth 2.0 인증**: 소셜 로그인을 통한 간편한 회원가입 및 로그인
- **JWT 토큰 관리**: Access/Refresh 토큰 기반의 안전한 사용자 인증
- **관리자 페이지 세션 관리**: 관리자 페이지 전용 세션 기반 인증

### 🗂 마이페이지
- **프로필 관리**: 닉네임 변경 및 중복 확인 기능 제공
- **냉장고 관리**: 식재료 검색 및 관리, 추천 레시피 검색
- **활동 내역**: 좋아요, 즐겨찾기한 레시피와 작성 댓글 기록 확인
- **알림 설정**: SSE 기반 실시간 알림 설정 관리

### 🔍 레시피 검색 및 추천
- **재료 기반 검색**: 사용자가 입력한 재료를 바탕으로 레시피 추천
- **자연어 처리**: 오타, 초성/중성 분리를 통해 정교한 검색 지원
- **세부 정보 제공**: 상세 조리 과정과 영양 정보 표시
- **이미지 최적화**: Firebase Storage를 활용한 레시피 이미지 최적화 제공
- **실시간 인기도**: RabbitMQ를 통한 실시간 좋아요 수 반영

### 🧊 냉장고 관리
- **식재료 등록**: 사용자가 보유한 재료를 등록 및 관리
- **자동완성 검색**: ElasticSearch 기반 식재료명 자동완성 기능
- **중복 확인**: 이미 등록된 재료 중복 등록 방지
- **레시피 추천**: ElasticSearch를 활용한 맞춤형 레시피 제공

### 📖 레시피/커뮤니티 조회페이지
- **회원 맞춤 서비스**: 회원/비회원 분리된 레시피 추천 리스트 제공
- **게시물 관리**: 커뮤니티 게시물 공개/비공개 설정 및 비회원 접근 제한
- **이미지 최적화**: 이미지 리사이징 및 포맷 최적화

### 👥 커뮤니티
- **게시물 관리**: 작성, 수정, 삭제, 조회 기능 지원
- **댓글 소통**: 사용자 간 댓글을 통한 소통 지원
- **실시간 알림**: 댓글 작성 시 게시물 작성자에게 실시간 알림 제공

---


## 🤝 협업 도구 & 워크플로우

### 🤼 협업 도구

**Task Management**

![Jira](https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=jira&logoColor=white)

**Documents**

![Confluence](https://img.shields.io/badge/Confluence-172B4D?style=for-the-badge&logo=confluence&logoColor=white)

**Communication**

![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)

### 💼 워크플로우
- 작업 관리: Jira를 활용해 작업 티켓 생성 및 상태 추적.
- 문서 관리: Confluence에 프로젝트 설계 및 기술 문서 기록.
- CI 파이프라인: GitHub Actions로 빌드 & 테스트를 통한 검증 자동화
- 코드 리뷰: PR 작성 및 최소 1명 이상의 승인 후 병합.

---

## 서버 아키텍처
### 배포 URL: https://nangpago.site

![alt text](assets/server_architecture.png)


## 📊 데이터셋 및 전처리
레시피 데이터셋
출처: 식품의약품안전처 조리식품 레시피 DB
데이터 전처리:
- 정규화식을 통한 전처리, 불필요한 열 제거 및 null 값 처리.
- 재료 리스트를 ElasticSearch에 최적화된 형태로 변환.

## 레시피 추천 흐름
- 사용자가 보유한 재료 입력.
- ElasticSearch에서 해당 재료를 포함한 레시피 검색.
- 정렬 및 필터링 후 사용자가 선호할 만한 결과 반환.

---

## 배포 환경
![alt text](assets/cicd.png)
- **CI/CD 파이프라인 구성:**
  - PR 생성 시 자동 Build/Test 수행.
  - Release 버전이 생성되면 자동으로 Docker 컨테이너 배포.
- **Docker Compose 활용:**
  - Backend, Frontend, DB, ElasticSearch 모두 컨테이너화.

---


## 📄 프로젝트 구조
```yaml
.
├── NangPaGo-be/ # 백엔드
├── NangPaGo-fe/ # 프론트엔드
└── NangPaGo-data/ # 데이터 처리
