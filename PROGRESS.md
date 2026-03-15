# PROGRESS

## Contexto
Esta nota consolida a análise do projeto em relação ao que foi pedido na documentação (README.md, FOLDERS.md, STAGES.md, INF.md) e o que existe no código.

## O que falta (principais itens)
- Endpoints essenciais ausentes: `/auth/login`, `/auth/refresh`, `/users`, `/accounts`, `/transactions`. Só existe `/auth/register`.
- Camadas ausentes: `service/`, `mapper/`, `exception/`, `audit/`.
- JWT não implementado: sem `JwtService`, `JwtAuthFilter`, `CustomUserDetailsService` e sem configuração de propriedades de JWT.
- Autorização por roles não implementada (apenas liberação básica em `SecurityConfig`).
- Auditoria não implementada: sem `AuditService`/`AuditAspect` e interceptação AOP.
- DTOs incompletos e sem validação (`@NotBlank`, `@Email`, etc.). `LoginResponse` e `RefreshRequest` estão vazios.
- DTOs sem getters/setters/records (pode impedir binding do JSON no Spring).
- Documentação OpenAPI/Swagger ausente (sem dependência `springdoc` e sem configuração para `/swagger-ui.html`).
- `Dockerfile` ausente (só há `docker-compose.yml`).
- Testes praticamente inexistentes (apenas `FinancesApiApplicationTests.java`, sem unitários e sem integração com Testcontainers).
- Regras de negócio não implementadas (saldo negativo, transações imutáveis, refresh token persistido, etc.).

## O que já existe (ok)
- Entidades JPA e enums em `finances-api/src/main/java/com/finances/finances_api/domain/` e `domain/enums/`.
- Repositórios em `finances-api/src/main/java/com/finances/finances_api/repository/`.
- Migrations Flyway em `finances-api/src/main/resources/db/migration/`.
- `docker-compose.yml` com PostgreSQL na raiz.
