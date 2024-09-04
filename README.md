# &nbsp; _RUNRUN_ <a><img src="https://github.com/user-attachments/assets/6fc35551-08c0-4e7e-a76c-d18145f9d214" align="left" width="100"></a>

&nbsp;&nbsp;
![pass the coverage](https://github.com/thdefn/twentysix/actions/workflows/coverage.yml/badge.svg)
[![codecov](https://codecov.io/github/thdefn/twentysix/branch/DEVELOP/graph/badge.svg?token=HAYBYM0Y4J)](https://codecov.io/github/thdefn/twentysix)


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
  <summary><strong>Version Detail</strong> as of 2024.09.04 </summary>

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


## User Flow

### Order Flow

<div style="display: flex; align-items: flex-start; gap: 20px;">
    <img src="https://github.com/user-attachments/assets/35400f7e-778f-4864-a981-7a82f4972571" alt="order flow" height="400" style="border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
    <div style="max-width: 500px;">
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
    </div>
</div>







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

| type      | 설명                                               |
|-----------|--------------------------------------------------|
| `feat`    | A new feature                                    |
| `test`    | Adding new test or making changes to existing test |
| `fix`     | A bug fix                                        |
| `perf`    | A code that improves performance                 |
| `docs`    | Documentation a related changes                  |
| `refactor` | Changes for refactoring                      |
| `build`   | Changes related to building the code             |
| `chore`   | Changes that do not affect the external user     |
