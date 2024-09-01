### API-DOCS
[**📝 click me**](https://thdefn.github.io/twentysix/api-docs.html)

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
