## 🚀 RUNRUN
![pass the coverage](https://github.com/thdefn/twentysix/actions/workflows/coverage.yml/badge.svg)
[![codecov](https://codecov.io/github/thdefn/twentysix/branch/DEVELOP/graph/badge.svg?token=HAYBYM0Y4J)](https://codecov.io/github/thdefn/twentysix)

### 선착순 구매 플랫폼 RUNRUN


### Documents

[**🔗 API Document**](https://thdefn.github.io/twentysix/api-docs.html)

[**🔗 ER-Diagram**](https://www.erdcloud.com/d/FgPnq6mf4pj36RgZJ)

### Tech Stack
**Deploy** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-social&logo=kubernetes&logoColor=white">
<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-social&logo=NGINX&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-social&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/Github Action-2088FF?style=for-the-social&logo=githubactions&logoColor=white">

**Language** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Python-3776AB?style=for-the-social&logo=python&logoColor=white">
<img src="https://img.shields.io/badge/Java-blue?logo=Java&logoColor=white"/>

**Frame Work** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-social&logo=Spring&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-social&logo=Gradle&logoColor=white">

**Data Source** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/MySQL-4479A1.svg?style=for-the-social&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/MongoDB-black?style=for-the-social&logo=mongodb">
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-social&logo=Redis&logoColor=white">

**Inter Communication** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-social&logo=apache-kafka&logoColor=white">
<img src="https://img.shields.io/badge/GRPC-4285F4?style=for-the-social&logo=google&logoColor=white">

**Coverage** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-social&logo=junit5&logoColor=white">
<img src="https://img.shields.io/badge/Codecov-F01F7A?style=for-the-social&logo=codecov&logoColor=white">
<img src="https://img.shields.io/badge/Redoc-6BA539?style=for-the-social&logo=openapiinitiative&logoColor=white">
<img src="https://img.shields.io/badge/Jacoco-blue?logo=Java&logoColor=white"/>

**ETC** &nbsp; **|** &nbsp;
<img src="https://img.shields.io/badge/Redoc-6BA539?style=for-the-social&logo=openapiinitiative&logoColor=white">
<img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=for-the-social&logo=JSON Web Tokens&logoColor=white">

#### Dependency

- java 22
- Gradle 8.8
- Spring Boot 3.3.2
- Kafka 3.8.0
- Redis 7.4
- gRPC 1.66.0



### User Flow

#### order process
<img src="https://github.com/user-attachments/assets/684d512b-0396-410e-ae93-32b422f00157" alt="order flow" height="500">


### Infrastructure Setup
`docker-compose -f ./infra/docker-compose-infra.yml up -d`


### Convention
#### Branch Strategy
```
main
├─hotfix
└─ develop (default)
    └─ DOMAIN/이슈번호
```

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
