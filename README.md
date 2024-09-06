# &nbsp; _RUNRUN_ <a><img src="https://github.com/user-attachments/assets/6fc35551-08c0-4e7e-a76c-d18145f9d214" align="left" width="100"></a>

&nbsp;&nbsp;
![Last commit](https://img.shields.io/github/last-commit/thdefn/twentysix?color=9437FF)
![pass the coverage](https://github.com/thdefn/twentysix/actions/workflows/coverage.yml/badge.svg)
[![codecov](https://codecov.io/github/thdefn/twentysix/branch/README%2F112/graph/badge.svg?token=HAYBYM0Y4J)](https://codecov.io/github/thdefn/twentysix)
![README Updated At](https://img.shields.io/badge/README%20updated%20at-2024%2009%2006-black)


<img src="https://github.com/user-attachments/assets/5838f7d5-acb6-459b-8181-6a4c95e50211" width="500">

## 선착순 주문은 _RUNRUN_ 에서❕

RUNRUN 은 상품에 대한 선착순 구매 기능을 지원하는 서비스입니다.<br/>
저희 서비스는 **높은 트래픽 상황에서도 유저에게 원활한 구매 경험을 제공**하는 것을 목표로 하고 있습니다.<br/>
서비스의 특징은 아래와 같습니다.

**상품 별 서킷 브레이커를 도입해 시스템 안정성 확보**  [**#92**](https://github.com/thdefn/twentysix/pull/92) </br>
주문 요청이 폭주되는 상황에서, 시스템의 안정성을 확보하기 위해 상품에 대해 서킷 브레이커를 적용했습니다. <br>
이를 통해 시스템의 신뢰성과 가용성을 유지하고, 대규모 트래픽 상황에서도 안정적으로 서비스를 제공할 수 있습니다.


**주문 서버의 자동 확장을 통한 유연한 대응**  [**#111**](https://github.com/thdefn/twentysix/pull/111) </br>
대규모 트래픽 상황에 적절히 대응하기 위해 주문 서버에 오토 스케일링을 적용했습니다. <br>
이를 통해 트래픽이 급증할 때 서버 리소스를 자동으로 확장하여 서비스 중단 없이 안정적인 주문 처리가 가능하도록 했습니다.


**재고 관리에 분산락을 적용해 데이터 무결성 보장**  [**#70**](https://github.com/thdefn/twentysix/pull/70) <br>
다수의 사용자가 동시에 주문을 시도하는 상황에서 데이터의 무결성을 보장하기 위해 분산 락을 적용했습니다. <br>
이를 통해 경쟁 조건을 방지하고, 정확한 재고 관리와 안정적인 주문 처리가 가능하도록 했습니다.



## Documents

[**🔗 API Document**](https://thdefn.github.io/twentysix/api-docs.html)

[**🔗 ER-Diagram**](https://www.erdcloud.com/d/FgPnq6mf4pj36RgZJ)

[**🔗 Coverage Report**](https://codecov.io/github/thdefn/twentysix)



## Technologies Used

**Deploy** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-social&logo=kubernetes&logoColor=white">
<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-social&logo=NGINX&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-social&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/Github Action-2088FF?style=for-the-social&logo=githubactions&logoColor=white">
<br/>
**Language** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Python-3776AB?style=for-the-social&logo=python&logoColor=white">
<img src="https://img.shields.io/badge/Java-blue?logo=Java&logoColor=white"/>
<br/>
**Frame Work** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-social&logo=Spring&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-social&logo=Gradle&logoColor=white">
<br/>
**Data Source** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/MySQL-4479A1.svg?style=for-the-social&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/MongoDB-black?style=for-the-social&logo=mongodb">
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-social&logo=Redis&logoColor=white">
<br/>
**Inter Communication** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-social&logo=apache-kafka&logoColor=white">
<img src="https://img.shields.io/badge/gRPC-4285F4?style=for-the-social&logo=google&logoColor=white">
<br/>
**Coverage** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-social&logo=junit5&logoColor=white">
<img src="https://img.shields.io/badge/Codecov-F01F7A?style=for-the-social&logo=codecov&logoColor=white">
<img src="https://img.shields.io/badge/Jacoco-blue?logo=Java&logoColor=white"/>
<br/>
**ETC** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Redoc-6BA539?style=for-the-social&logo=openapiinitiative&logoColor=white">
<img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=for-the-social&logo=JSON Web Tokens&logoColor=white">


<details>
  <summary><strong>Version Detail</strong></summary>

- Java `22`
- Python `3.9`
- Gradle `8.8`
- Spring Boot `3.3.2`
- Kafka `3.8.0`
- MySQL `8.0`
- MongoDB `7.0.12`
- Redis `7.4`
- gRPC `1.66.0`

</details>


## Performance Optimization

### Circuit Breaker

상품 별 서킷 브레이커를 도입해 시스템의 안정성을 향상시키고 서버 리소스를 절약했습니다.

<img alt="test-case" src="https://github.com/user-attachments/assets/b22ac07d-570d-4e0f-a9f7-d121bc2188cc">

- 10,000명의 사용자와 초당 300명의 유저를 가정한 고부하 환경에서 성능 테스트를 진행했습니다.
- 테스트는 주문 요청, 결제 페이지 진입, 결제 요청의 순서로 수행되었습니다.

#### Before Applying Circuit Breaker

<img alt="before-circuit" src="https://github.com/user-attachments/assets/63c97bb4-44fc-4316-9688-b53b4f359f44">

- 서킷 브레이커 도입 전에는 대규모 사용자가 몰릴수록 주문 서버에서 커넥션 에러가 빈번하게 발생했습니다.
- 전체 에러 중 67%가 처리되지 않은 커넥션 에러였으며, 최대 응답 시간은 약 70초에 달했습니다.
- 주문 서버의 병목 현상으로 인해, 주문 서버와 연동된 결제 API의 응답 시간도 지연되었습니다.


#### After Applying Circuit Breaker
<img alt="after-circuit" src="https://github.com/user-attachments/assets/63f1438f-c106-4065-9cc6-650713174f70">

- 상품별 서킷 브레이커를 도입하여, 특정 상품 주문에서 10%의 에러가 발생하면 30초 동안 해당 상품을 포함한 주문에 대해 즉시 에러 응답을 하도록 설정했습니다. 
- 메인 주문 로직 이전에 서킷 브레이커가 작동해 빠르게 오류를 감지하고, 불필요한 서버 자원 소모를 줄였습니다.
- 그 결과 커넥션 에러는 1% 이하로 감소했으며, 최대 응답 시간도 30초 이내로 줄어 서킷 브레이커 도입 전보다 50% 향상되었습니다.

## Technical Decision

### Distributed Lock : Redisson Lock

|               | **Redis Custom Lock**                                                      | **Redisson Lock**                                  | **Redis의 싱글 스레드 특성 활용**                 |
|---------------|----------------------------------------------------------------------------|----------------------------------------------------|-----------------------------------------|
| **장점**       | - 간단한 구현 <br/>                                           | - Pub/Sub을 활용하여 네트워크 요청 최소화  | - 원자적 작업 보장       <br/> - 가장 적은 네트워크 비용 |
| **단점**       | - 지속적인 네트워크 요청 필요 <br/> | - Redisson 라이브러리에 대한 학습 필요   | - 클러스터 환경에서 사용 불가 <br/> - 코드 복잡도 증가     |


- 데이터 무결성을 보장하기 위해 Redis를 활용해 분산 락을 구현했습니다.
  - Redis는 메모리 기반 데이터 저장소로, 매우 빠른 읽기/쓰기 성능을 제공하여 락을 신속하게 획득하고 해제할 수 있습니다.
- MSA 환경에서 노드가 확장될 때, 락 메커니즘을 확장할 수 있어야 합니다.
- 따라서 네트워크 오버헤드가 적고, 클러스터 환경에서도 안정적으로 동작할 수 있는 Redisson 방식을 선택했습니다.


### Direct Messaging : gRPC

|        | **gRPC**                                      | **HTTP**               |
|--------|-----------------------------------------------|------------------------|
| **장점** | - 인코딩/디코딩 속도가 빠름        <br/> - 통신 효율성 높음 <br/> | - 상대적으로 쉬운 기술 도입       |
| **단점** | - 러닝 커브가 있음 <br/> - 설정과 학습 필요        | - 높은 트래픽에서 성능 저하 가능성 |


- MSA 환경은 서버 간 통신이 높은 성능과 낮은 지연 시간을 요구합니다. 
  - 각 도메인 간 데이터 교환이 빈번하게 이루어지기 때문에, 통신이 느리면 전체 시스템의 응답 시간이 지연될 수 있습니다.
- 따라서 도입에는 러닝 커브가 있지만, 높은 성능과 낮은 지연 시간을 충족할 수 있는 gRPC를 선택했습니다.

## Architecture


### Infra Structure Diagram
<img width="1030" alt="infra-structure" src="https://github.com/user-attachments/assets/5112face-6297-4a6d-aae2-5eefb7da43de">


### Kubernetese Architecture Diagram
<img width="1030" alt="k8s-architecture" src="https://github.com/user-attachments/assets/23c8fa7b-d87d-4ac5-ac09-f9997e4c96b4">

### Directory Structure
```
 ┣ 📂 .github
 ┃ ┗ 📂 workflows
 ┣ 📂 gateway
 ┣ 📂 user
 ┃ ┃ 📂 src/main/resources
 ┃ ┃ ┗ 📜 application-dev
 ┃ ┃ ┗ 📜 application-test
 ┃ ┣ 📜 Dockerfile-dev
 ┃ ┗ 📜 Dockerfile-test
 ┣ 📂 brand
 ┣ 📂 order
 ┣ 📂 payment
 ┣ 📂 product
 ┣ 📂 cron-jobs
 ┣ 📂 kubernetese
 ┃ ┃ 📂 autoscaler
 ┃ ┣ 📂 cronjob
 ┃ ┣ 📂 deployment
 ┃ ┣ 📂 ingress
 ┃ ┗ 📂 service
 ┣ 📂 load-test
 ┃ ┗ 📜 docker-compose-load-test.yml
 ┣ 📂 infra
 ┣ 📜 build.gradle
 ┣ 📜 settings.gradle
 ┗ 📜 .gitignore
```

- `📂 workflows` : 테스트 커버리지 체크와 API 문서 통합 빌드를 자동화하는 워크플로우 파일이 포함되어 있습니다.
- `📂 gateway` : 사용자의 인증과 권한 부여를 담당하는 엔드포인트 서버입니다.
- `📂 user` : 회원 가입, 로그인, 회원 정보 관리 등을 담당합니다.
- `📂 brand` : 상점 생성, 수정, 조회 등 판매자의 상점 관리를 담당합니다.
- `📂 order` : 유저의 상품 주문, 조회, 취소, 장바구니 등을 담당합니다.
- `📂 payment` : 결제 요청, 결제 취소 등을 담당합니다.
- `📂 product` : 상품 등록, 조회, 수정, 삭제 등을 담당합니다.
- `📂 cron-jobs` : 주기적으로 실행되는 배치 작업을 담당하는 스크립트를 포함합니다.
- `📂 kubernetese` : 쿠버네티스 관련 구성 파일과 스크립트가 모여 있는 폴더입니다.
- `📂 load-test` : 부하 테스트 스크립트와 도커 컴포즈 구성 파일을 포함합니다.
- `📂 infra` : 서버의 전체 인프라를 구성하는 데 필요한 도커 컴포즈 파일을 포함합니다. 이 폴더는 인프라 배포와 설정을 자동화하는 데 사용됩니다.
- `📂 **/📜application-dev.yml` : 개발 환경에 반영될 환경변수를 세팅합니다. 모든 서버 모듈이 포함하고 있습니다.
- `📂 **/📜application-test.yml` : 테스트 환경에 반영될 환경변수를 세팅합니다. 모든 서버 모듈이 포함하고 있습니다.
- `📂 **/📜Dockerfile-dev.yml` : 개발 환경에 쓰일 도커 이미지를 빌드합니다. 모든 서버 모듈이 포함하고 있습니다.
- `📂 **/📜Dockerfile-test.yml` : 테스트 환경에 쓰일 도커 이미지를 빌드합니다. 모든 서버 모듈이 포함하고 있습니다.
- `📜 build.gradle` : 하위 모듈에 공통으로 필요한 종속성을 선언합니다. 각 서버 간의 의존성을 최소화하기 위해, 하위 모듈의 테스트 커버리지 통합과 같은 필수적인 작업만을 포함합니다.
- `📜 settings.gradle` : 하위 모듈을 선언합니다.
- `📜 .gitignore` : git 에 올라가지 않아야 할 파일을 정의합니다.
- `📜 docker-compose-load-test.yml` : 부하 테스트 환경을 위한 서버 컨테이너와 모니터링 도구 등을 정의합니다.


## User Flow

### Order Flow

#### Flow Chart

<table>
  <tr>
    <td>
       <img src="https://github.com/user-attachments/assets/e5311023-dffd-49b7-bc0c-bd68d4989dca" alt="order flow" height="550" style="border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
    </td>
    <td>
      <p style="margin: 0; font-size: 1.1em; line-height: 1.6;">
        <strong>주문 실패</strong><br>
        1. 주문 시, 상품에 대한 재고를 체크합니다.<br>
        2. 재고가 부족하다면 유저는 주문 실패 응답을 받습니다.<br><br>
        <strong>결제 실패</strong><br>
        1. 결제 시, 상품에 대한 재고를 체크합니다.<br>
        2. 재고가 부족하다면 유저는 결제 실패 응답을 받습니다.<br>
        3. PG사에 결제를 요청합니다.<br>
        4. 잔액 부족 등 고객 귀책으로 인한 최종 결제 실패 시 유저는 결제 실패 응답을 받습니다. <br>
      </p>
    </td>
  </tr>
</table>

<br>

#### Sequence Diagram


![sequence-diagram](https://github.com/user-attachments/assets/62dc0123-e472-4d12-8264-67f79c737528)

## Convention
### Branch Strategy
```
main
├─hotfix
└─ develop (default)
    └─ DOMAIN/이슈번호
```

### Commit Convention
#### Commit Message
```javascript
<type>: <description>

[optional body]
```

#### Commit Type

| type      | description                                               |
|-----------|--------------------------------------------------|
| `feat`    | A new feature                                    |
| `test`    | Adding new test or making changes to existing test |
| `fix`     | A bug fix                                        |
| `perf`    | A code that improves performance                 |
| `docs`    | Documentation a related changes                  |
| `refactor` | Changes for refactoring                      |
| `build`   | Changes related to building the code             |
| `chore`   | Changes that do not affect the external user     |
