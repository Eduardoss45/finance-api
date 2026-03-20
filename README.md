# 💰 Finances API — Sistema Financeiro com Spring Boot

API REST para gestão financeira construída com **Spring Boot**, focada em **segurança, consistência transacional e arquitetura em camadas**.

O sistema permite:

* autenticação com JWT (access + refresh)
* gestão de usuários e contas
* registro de transações financeiras
* auditoria de ações sensíveis

O projeto foi desenvolvido com foco em **clareza arquitetural e decisões técnicas explícitas**, não apenas entrega funcional.

---

## ⚠️ Configuração Obrigatória (Critical Setup)

> A aplicação **não funciona corretamente** sem configuração adequada das variáveis abaixo.

### Variáveis obrigatórias

```env
JWT_SECRET=your-secret-key
```

* Utilizada para assinatura dos tokens JWT
* Deve ser consistente entre ambientes
* Valor inválido quebra autenticação silenciosamente

---

### ⚠️ Credenciais e ambiente

* Credenciais do banco estão em claro (`application.properties`)
* Uso **exclusivo para desenvolvimento**
* Não adequado para produção

---

## 🐳 Docker & Execução

### ⚠️ Estrutura de docker-compose

Existem dois arquivos:

| Caminho                            | Função                      |
| ---------------------------------- | --------------------------- |
| `/docker-compose.yml`              | Apenas PostgreSQL           |
| `/finances-api/docker-compose.yml` | **API + Banco (principal)** |

### Execução recomendada

```bash
cd finances-api
docker compose up --build
```

---

## 🧱 Arquitetura

```text
Cliente
  │
  ▼
Spring Boot API
  │
  ├── Security (JWT + Rate Limit)
  ├── Controllers
  ├── Services
  ├── Audit (AOP)
  ├── Exception Handling
  └── Persistence (JPA + PostgreSQL + Flyway)
```

### Padrão adotado

Arquitetura em camadas:

```
Controller → Service → Repository → Domain
```

---

## 🔐 Segurança

### Implementação

* JWT (access + refresh)
* BCrypt para hashing de senha
* Roles:

  * `ROLE_USER`
  * `ROLE_ADMIN`

---

### Tokens

| Tipo          | Expiração |
| ------------- | --------- |
| Access Token  | 1 hora    |
| Refresh Token | 7 dias    |

### Características

* Refresh token **persistido em banco**
* **Rotação ativa de refresh token**

---

### Rate Limiting (Diferencial)

Aplicado em endpoints críticos:

| Endpoint        | Limite     |
| --------------- | ---------- |
| `/auth/login`   | 5 req/min  |
| `/auth/refresh` | 10 req/min |

Implementado via filtro customizado.

---

## 📌 Endpoints

### Auth

```
POST /auth/register
POST /auth/login
POST /auth/refresh
```

---

### Users

```
GET /users
GET /users/{id}
PATCH /users/{id}/deactivate
```

Regras:

* ADMIN lista usuários
* usuário acessa apenas seus próprios dados

---

### Accounts

```
POST /accounts
GET /accounts
GET /accounts/{id}
DELETE /accounts/{id}
```

---

### Transactions

```
POST /accounts/{id}/transactions
GET /accounts/{id}/transactions
```

Regras:

* valores positivos
* não permite saldo negativo
* transações são imutáveis

---

## 📄 Paginação

```http
GET /accounts?page=0&size=10&sort=createdAt,desc
```

---

## 🗄️ Persistência

### Stack

* PostgreSQL
* Spring Data JPA
* Hibernate
* Flyway

---

### Flyway

```properties
baseline-on-migrate=true
clean-disabled=true
```

---

## ⚠️ Decisões Arquiteturais & Trade-offs

Esta seção documenta **decisões conscientes do projeto**.

---

### 1. Saldo persistido em conta

```text
accounts.balance
```

#### Decisão

Saldo armazenado diretamente na entidade.

#### Motivação

* leitura rápida
* menor custo computacional

#### Trade-off

* exige controle rigoroso de consistência
* risco de divergência se regras falharem

---

### 2. Controle de concorrência (Diferencial)

Uso de:

```sql
SELECT ... FOR UPDATE
```

#### Objetivo

* evitar race conditions em transações financeiras

---

### 3. `ddl-auto=update` ativo

```properties
spring.jpa.hibernate.ddl-auto=update
```

#### Motivo

* agilidade no desenvolvimento

#### Trade-off

* conflita com Flyway como fonte única de schema
* **não recomendado em produção**

---

### 4. Logging detalhado de segurança

```properties
logging.level.org.springframework.security=TRACE
```

#### Uso

* debugging de autenticação

#### Trade-off

* alto volume de logs
* possível exposição de informações sensíveis

---

## 🧾 Auditoria (Diferencial)

Implementada via **AOP com anotação `@Audited`**.

### Eventos registrados

* login
* criação de usuário
* criação de conta
* transações
* ações administrativas

### Estrutura

```
user_id
action
entity
entity_id
timestamp
```

---

## ❗ Tratamento de Erros

Padrão adotado:

* `ProblemDetail` (RFC 7807)
* `ValidationErrorResponse`

### Benefícios

* respostas consistentes
* fácil integração com frontend
* padronização de erros

---

## 📊 Observabilidade

### Implementado

* logs de aplicação
* logs detalhados de segurança

### Limitação atual

* ❌ não há correlação de requisições (trace-id)

> A documentação anterior sugeria correlação completa — isso foi **corrigido para refletir o estado real do projeto**.

---

## 🧪 Testes

### Estratégia

* unitários → camada service
* integração → endpoints

### Stack

* JUnit
* Testcontainers (PostgreSQL real)

---

## 📑 Swagger

Disponível em:

```
/swagger-ui.html
```

### Inclui

* endpoints
* DTOs
* autenticação Bearer

---

## ⭐ Diferenciais do Projeto

Itens além do escopo básico:

* Rate limiting por endpoint
* Auditoria via AOP
* Refresh token com rotação
* Lock pessimista em transações
* Paginação consistente
* Tratamento de erro padronizado (RFC 7807)

---

## 🧠 Considerações Arquiteturais

O projeto segue abordagem:

> **Monólito modular orientado a domínio**

Motivações:

* simplicidade operacional
* menor overhead
* facilidade de manutenção

---

## 🚀 Possíveis Evoluções

* Redis para cache
* fila assíncrona (RabbitMQ/Kafka)
* correlation-id (observabilidade completa)
* métricas com Micrometer
* tracing distribuído
* CI/CD pipeline

---

## 📌 Conclusão

Este projeto demonstra:

* domínio do ecossistema Spring
* aplicação de segurança real (JWT + rotação)
* controle de concorrência
* clareza arquitetural
* decisões técnicas explícitas
