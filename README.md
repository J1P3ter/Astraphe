# ⚡ J1P3ter - Astraphe
![](https://velog.velcdn.com/images/ayoung3052/post/c8aefa5c-0fd9-439f-94f8-4fa9bcd1e85d/image.png)

## 기간 한정 판매를 순식간에 처리하는 구매 플랫폼

판매 시작과 동시에 몰려드는 트래픽을 **대기열 서버**로 효율적으로 관리합니다.
또한, 대량 주문 데이터를 Kafka 기반의 **메시징 시스템**으로 빠르게 처리하여 모든 사용자가 놓치지 않고 상품을 구매할 수 있도록 최적화된 경험을 제공합니다.

---

# 🔧 서비스 아키텍처 & CI/CD 파이프라인

![](https://velog.velcdn.com/images/ayoung3052/post/48ac24b9-7a9a-4dd0-ac7a-37a6911ca756/image.png)

<br/>

# 🌩️ Astraphe 주요 기능

### **상품 판매 개시 시점에 몰리는 트래픽을 수용하기 위한 대기열 서버**

- **상품 별로 별도의 대기열** 관리
- 사용자는 상품 페이지가 아닌 `QueueHandler`에 먼저 접근
    - 대기열에 사람이 없을 경우 바로 `product` 로 연결
    - 대기열에 사람이 있을 경우 대기열에 등록
        - 이미 등록된 사용자가 다시 등록을 시도할 경우 맨 뒤 순위로 재 지정
- 상품 등록 시 **스케줄러**에 상품이 등록
    - 상품에 등록된 **판매 개시 시간** `startSaleTime` 의 1분 뒤부터 정해진 시간 마다 정해진 인원이 대기열에서 삭제, 상품으로의 접근이 허용 (`allowUser`)
- 상품 접근 **토큰 검증**
    - 대기열을 거치지 않고 상품 페이지로 접근 불가능
        - 스케줄러를 통한 `allowUser` 수행 후, `waitingRoom` (대기 중 페이지, 대기 순서 출력)에 있는 유저에게 `getToken` 을 통해 상품 접근 용도의 토큰을 발행
        - `gateway`에서 토큰 검증 후 유효한 토큰일 경우에만 상품 페이지로 접근 가능

### **상품 주문을 비동기적으로 빠르게 처리하기 위한** **메시징 시스템**

- 상품 주문 시 **주문 생성** → **재고 감소** → **주문 완료** 일련의 프로세스를 `Kafka`를 사용해 비동기적으로 처리
- 상품 주문 시 `재고 감소 토픽`에 메시지 전달
- `재고 감소 리스너`에서 재고 감소 후 `Success & Failed 토픽`에 메시지 전달
- `재고 감소 결과 리스너`에서 주문 상태 변경

### **이미지 업로드**

- 상품 등록 및 업데이트 시 상품 이미지와 상품 설명 이미지를 필요 시 첨부해서 `AWS S3` 버킷에 이미지를 업로드
- 업로드 후 이미지에 대한 URL만 DB로 관리

### **모니터링**

- `Prometheus` & `Grafana`를 사용해 각 서버 모니터링 시각화
- `Grafana`와 `Slack`을 활용해 서버 Up & Down에 대한 알람 처리

<br/>

# ✍️ 기술적 의사결정

### **MSA와 WebFlux 선택**

- **MSA**
    - 서비스 확장성과 독립적인 배포를 고려하여 **마이크로서비스 아키텍처**를 ****채택.
    - 높은 부하가 걸리는 부분만 수평적 확장 가능.
    - 전체 시스템을 확장하지 않아 효율적인 자원 사용 가능.
    - 특정 마이크로서비스만 업데이트하거나 롤백할 수 있어 점진적 배포 가능.
- **WebFlux (queue-server에만 적용)**
    - 반응형, 비동기적인 웹 애플리케이션 개발을 지원하는 모듈
        - 작업 완료, 데이터 사용 가능 등의 알림에 반응한다.
    - 새로운 이벤트 발생 시 생성된  **이벤트 스트림**을 구독하여 이벤트 처리
    - **Non-Blocking Request**를 수행하여 결과가 반환되지 않더라도 다른 작업을 수행할 수 있어, 많은 트래픽이 몰리는 서버를 처리하기에 적절하다.

### **PostgreSQL과 Redis 선택**

- **PostgreSQL**
    - 구조적 데이터를 다루기 용이하며, 확장성 있는 쿼리 성능을 제공하기 때문.
- **Redis**
    - 캐싱 시스템을 통해 실시간 재고 관리 및 성능 최적화.
    - 사용자 조회 성능 개선.
    - 비동기, 논블로킹 프로그래밍의 `WebFlux`를 사용할 경우에는 비동기적인 특성을 지원하는 `reactive Redis` 를 사용해야 한다.

### **Kafka** 선택

- **Kafka**
    - 주문 생성, 재고 감소 요청, 주문 상태 변경 등의 일련의 프로세스를 동기적으로 처리할 경우 서비스 응답 시간이 길어지기 때문에 **비동기적**으로 처리하기 위해 `메시지 큐` 도입 결정
    - `RabbitMQ`가 지정된 수신인에게 메시지를 전달하는 것과 달리, `Kafka`는 **Producer**가 메시지를 생산해두고 **Consumer**가 구독하고 있는 형태기 때문에 프로세스 변경에 따른 확장성이 좋다고 생각해 `Kafka`를 선택

### **JWT를 활용한 유저 인증**

- **JWT**
    - Session 인증 방식은 Server에서 Session 상태를 저장하고 관리해야 한다는 단점이 존재.
    - JWT 인증 방식은 Stateless로서 Server에서 Session에 대한 정보를 저장하지 않고 
    Client가 요청 시 전달한 JWT만 검증하면 인증 처리가 끝나기 때문에 JWT 인증 방식을 선택.

### **CI/CD 도입**

- **GitHub Actions**
    - GitHub Repository와 직접 통합되어, 코드 변경 시 자동으로 빌드, 테스트, 배포 등 다양한 작업을 트리거할 수 있기 때문.
- **Docker Hub**
    - ECR은 AWS에 최적화된 Container Registry이지만, 이 프로젝트를 EC2가 아닌 Azure, GCP 등에 배포할 가능성도 있기 때문.

<br/>

# 🎯 트러블 슈팅

[대기열 서버로 진입 / product로 진입 비교 부하 테스트](https://velog.io/@ayoung3052/WebFlux-%EB%8C%80%EA%B8%B0%EC%97%B4-%EC%84%9C%EB%B2%84%EB%A1%9C-%EC%A7%84%EC%9E%85-MVC-%EB%B0%94%EB%A1%9C-%EC%A7%84%EC%9E%85-%EB%B9%84%EA%B5%90-%EB%B6%80%ED%95%98-%ED%85%8C%EC%8A%A4%ED%8A%B8)

[Gateway: 잘못된 url로 요청 시 응답 데이터 처리](https://velog.io/@dmitry__777/Gateway-%EC%9E%98%EB%AA%BB%EB%90%9C-url%EB%A1%9C-%EC%9A%94%EC%B2%AD-%EC%8B%9C-%EC%9D%91%EB%8B%B5-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%B2%98%EB%A6%AC)

[**Kafka** ServletRequestAttributes 사용 불가로 인한 Auditor 이슈 해결](https://velog.io/@lukeydokey/Kafka-ServletRequestAttributes-NullPointer-Exception)

[대기열 시스템을 위한 ReactiveRedis factory 분리](https://velog.io/@ayoung3052/%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88-WebFlux-%EB%B9%84%EB%8F%99%EA%B8%B0-MVC-%EB%8F%99%EA%B8%B0-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-Redis-%EC%84%A4%EC%A0%95)

[주문 등록 시 한 개를 저장 후 두 개가 보이는 오류](https://blog.naver.com/thffo123/223630354351)

<br/>

# 🤖 기술 스택

### 언어 및 프레임워크
<img src="https://img.shields.io/badge/Java 17.0.12-007396?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/Spring_Boot 3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> <img src="https://img.shields.io/badge/QueryDSL 5.0.0-00843D?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/JPA 3.3.4-59666C?style=for-the-badge&logo=hibernate&logoColor=white">

### 빌드 툴
<img src="https://img.shields.io/badge/Gradle 8.10.2-02303A?style=for-the-badge&logo=gradle&logoColor=white">

### DB
<img src="https://img.shields.io/badge/PostgreSQL 17.0-336791?style=for-the-badge&logo=postgresql&logoColor=white"> <img src="https://img.shields.io/badge/AWS S3-FF9900?style=for-the-badge&logo=amazons3&logoColor=white">

### 캐싱
<img src="https://img.shields.io/badge/Redis 7.4.1-DC382D?style=for-the-badge&logo=redis&logoColor=white">

### 인프라 및 배포
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/Docker 27.3.1-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Docker Compose v2.29.7-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Docker Hub-2496ED?style=for-the-badge&logo=docker&logoColor=white">

### 메시징 시스템
<img src="https://img.shields.io/badge/Zookeeper 3.4.6-FF9900?style=for-the-badge&logo=apachezookeeper&logoColor=white"> <img src="https://img.shields.io/badge/Kafka 2.13 2.6.3-231F20?style=for-the-badge&logo=apachekafka&logoColor=white">

### 협업 툴 및 소스 관리
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">

### 성능 및 부하 테스트
<img src="https://img.shields.io/badge/Apache JMeter-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white">

### 모니터링 및 알람
<img src="https://img.shields.io/badge/Prometheus 2.54.1-E6522C?style=for-the-badge&logo=prometheus&logoColor=white"> <img src="https://img.shields.io/badge/Grafana 11.2.2-F46800?style=for-the-badge&logo=grafana&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white">

### 결제
<img src="https://img.shields.io/badge/Toss%20Payments-0064FF?style=for-the-badge&logoColor=white">

### MSA
<img src="https://img.shields.io/badge/Eureka Netflix-000000?style=for-the-badge&logo=netflix&logoColor=white">

### API 테스트
<img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"> <img src="https://img.shields.io/badge/Swagger--UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">

<br/>

# 🐋 Docker-compose 실행 방법

1. Docker compose files ZIP 다운로드 <br>
    [📁astraphe.zip](https://github.com/user-attachments/files/17486663/astraphe.zip) <br>
    위 링크 작동 안 할 시 [📁astraphe.zip](https://github.com/user-attachments/files/17486663/astraphe.zip)

2. docker network 생성
    1. `ex) docker network create --driver=bridge astraphe_network`
    2. 이때 Infra와 Server 모두 **동일한 network** 사용
3. docker-compose.yaml 내 `{ }` 로 감싸진 부분 작성
    1. Infra
        1. { POSTGRES_USER }
        2. { POSTGRES_PASSWORD }
        3. { DOCKER_NETWORK_NAME } - 생성한 네트워크 이름
        4. { REDIS_PASSWORD } - Redis container의 command 내 존재
    2. Server
        1. { DOCKER_NETWORK_NAME }
        2. { POSTGRESQL_ID }
        3. { POSTGRESQL_PASS }
        4. { REDIS_PASSWORD }
        5. { AWS_ACCESSKEY }
        6. { AWS_SECRETKEY }
        7. { AWS_REGION }
        8. { AWS_S3_BUCKETNAME }
4. docker compose -f docker-compose.yaml up -d
    1. 순서 : Infra → Server
- **Infra 구성**
    - postgres
    - redis
    - zookeeper
    - kafka
    - kafka-ui
    - prometheus
    - grafana
- **Server 구성**
    - Eureka
    - Gateway
    - User
    - Product
    - Order
    - Queue

<br/>

# 📘 Notion

[J1P3ter Notion](https://www.notion.so/13-J1P3ter-a8c73f0676c54a08ac08d8eedce4c98e?pvs=21) 

# 👥 CONTRIBUTORS
| 이름     | 역할                     | GitHub                                      | 기여점                                                                                                                      |
| -------- | ------------------------ | ------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| 박영무   | `LEADER` <br> `BACKEND_DEVELOPER` | [VoiceofSiren](https://github.com/VoiceofSiren)     | ▶**사용자 서버**<br>- `Spring Data JPA`와 `QueryDsl`을 사용하여 CRUDS 구현<br>- `Redis`를 이용하여 사용자 정보를 캐싱, 조회 성능 개선<br>- `AOP`를 적용하여 회원가입/로그인 시 요청 데이터의 유효성 검사<br>- 사용자 로그인 시 JWT 발급<br>▶**인증/인가 및 라우팅**<br>- Gateway에서 GlobalFilter 구현 클래스를 이용하여 JWT 필터링 처리<br>- Gateway에서 `권한 기반 라우팅`을 통해 비즈니스 로직에서의 인가 로직을 최소화<br>▶**CI/CD 파이프라인 구축**<br>- `GitHub Actions`와 Docker Hub를 이용하여 EC2 인스턴스에 배포<br>- GitHub Repository Settings에 secret 값 추가<br>- main 브랜치에 push하는 것을 트리거하여 CI/CD가 동작하도록 설정 |
| 송형근   | `SUB_LEADER` <br> `BACKEND_DEVELOPER` | [lukeydokey](https://github.com/lukeydokey) |▶ **Common Module**<br>- `Multi-module` 구조에서 공통으로 사용할 부분을 Common Module에 정의해서 Import 하도록 적용<br>▶ **Product Server**<br>- Company, Product CRUD 구현<br>- 통합 테스트 코드 작성<br>- `data.sql`을 사용해 Category의 기본 데이터 주입<br>- 검색 & 정렬 조건이 여럿이라 확장성 있게 QueryDSL 적용해서 Product의 검색 구현<br>▶ **AWS S3**<br>- `S3 Bucket`을 활용해 상품, 상품 설명 이미지 업로드<br>▶ **Kafka**<br>- 주문 → 재고 감소 → 주문 상태 변경 프로세스를 Queue 형태로 이어서 처리하기 위해 `Kafka` 적용<br>- Listener와 ErrorHandler를 사용해 재고 감소 성공/실패 로직 구현<br>▶ **Monitoring**<br>- `Prometheus` & `Grafana`를 사용해 각 서버 모니터링 시각화<br>- Grafana와 Slack 을 활용해 서버 Up & Down 에 대한 알람 처리<br>▶ **Deployment**<br>- EC2 환경에서 배포가 가능하도록 docker-compose.yaml 작성<br>- CI/CD 파이프라인 진행 간 발생한 이슈들 트러블 슈팅<br>                                       |
| 곽슬래   | `BACKEND_DEVELOPER`         | [lossol1](https://github.com/lossol1)       |**▶ 주문**<br>- 주문 생성 후, 재고 감소와 결제 프로세스를 Queue 형태로 처리하기 위해 `Kafka`를 도입하여 비동기 처리<br>- 주문 단계에서 발생하는 재고 차감 및 결제 이벤트를 순차적으로 처리하여 안정성과 효율성 증대<br>**▶결제**<br>- `Toss Payments API`를 적용하여 결제 서비스 구현<br>PG사 연결을 통해 다양한 결제 수단 지원 및 결제 처리 안정성 강화                                                              |
| 조아영   | `BACKEND_DEVELOPER`         | [ayboori](https://github.com/ayboori)       |**▶ 대기열 시스템**<br>- `QueueHandler`<br>> 대기열에 사람이 없을 경우 바로 product 로 연결<br>> 대기열에 사람이 있을 경우 대기열에 등록<br>-  로그인 사용자의 `productId` 별 rank 조회 (대기 방)<br>- `productId` 별 대기 목록 조회 / 접근 허용 여부 조회<br><br>- 상품 등록 시 **스케줄러**에 상품 등록 / 동적 스케줄링<br>> 상품에 등록된 판매 개시 시간<br>`startSaleTime` 의 1분 뒤부터 정해진 시간 마다 정해진 인원의 접근 허용<br>> 수동으로 스케줄링 시작 / 정지 API도 호출 가능<br><br>- **상품 접근 토큰 검증**<br>> 대기열을 거치지 않고 상품 페이지로 접근 불가능<br>> `gateway`에서 토큰 검증 후 유효한 토큰일 경우에만 상품 페이지로 접근 가능                                                   |

