# Desafio Backend — API Financeira Enterprise com Spring Boot

Este desafio tem como objetivo desenvolver uma **API REST corporativa utilizando Java e Spring Boot**, explorando conceitos fundamentais do ecossistema Spring.

O foco não é apenas implementar funcionalidades, mas **entender como o framework funciona internamente**, incluindo:

- Injeção de dependência
- Ciclo de vida de beans
- Arquitetura em camadas
- Segurança com JWT
- Persistência com JPA/Hibernate
- Observabilidade e auditoria
- Testes e boas práticas enterprise

---

# 🎯 Objetivo do Desafio

Construir uma **API de gestão financeira simplificada**, onde usuários podem possuir contas e registrar transações.

O sistema deve demonstrar:

- Domínio do **ecossistema Spring**
- Arquitetura backend madura
- Segurança consistente
- Separação clara de responsabilidades
- Organização voltada para aplicações empresariais

---

# 📚 Tecnologias Obrigatórias

## Framework

- Spring Boot
- Spring Framework

## Persistência

- Spring Data JPA
- Hibernate ORM
- PostgreSQL

## Segurança

- Spring Security
- JWT

## Infraestrutura

- Docker
- Flyway

## Testes

- JUnit
- Testcontainers

---

# 🧠 O que você deve aprender com este projeto

Este projeto foi desenhado para forçar o aprendizado de conceitos centrais do Spring:

### Inversão de Controle

Entender como o Spring gerencia objetos através do **IoC Container**.

Você deve compreender:

- Beans
- ApplicationContext
- Dependency Injection

---

### Arquitetura em Camadas

Separação clara entre responsabilidades:

```
Controller
Service
Repository
Domain
```

Cada camada possui responsabilidades específicas:

| Camada     | Responsabilidade  |
| ---------- | ----------------- |
| Controller | Interface HTTP    |
| Service    | Regras de negócio |
| Repository | Acesso ao banco   |
| Domain     | Modelo do sistema |

---

### Persistência com ORM

Aprender como o Spring integra com o Hibernate para:

- mapear entidades
- gerar queries
- gerenciar transações

---

### Segurança

Compreender o funcionamento do pipeline de segurança:

```
HTTP Request
↓
Security Filter Chain
↓
Authentication
↓
Authorization
↓
Controller
```

---

### Observabilidade

Aplicações enterprise precisam de:

- logs estruturados
- auditoria de ações
- rastreabilidade

---

# 🧱 Requisitos Funcionais

## Autenticação

Sistema deve possuir:

```
POST /auth/register
POST /auth/login
POST /auth/refresh
```

Regras:

- Senhas criptografadas com BCrypt
- Token JWT com expiração
- Refresh token persistido

---

## Gestão de Usuários

Endpoints:

```
GET /users
GET /users/{id}
PATCH /users/{id}/deactivate
```

Regras:

- Apenas ADMIN pode listar usuários
- Usuário pode visualizar apenas seus próprios dados

---

## Gestão de Contas

Endpoints:

```
POST /accounts
GET /accounts
GET /accounts/{id}
DELETE /accounts/{id}
```

Regras:

- Cada usuário pode possuir múltiplas contas
- Conta pertence a apenas um usuário

---

## Transações

Endpoints:

```
POST /accounts/{id}/transactions
GET /accounts/{id}/transactions
```

Tipos:

```
CREDIT
DEBIT
```

Regras de negócio:

- Não permitir saldo negativo
- Transações são imutáveis
- Valores devem ser positivos

---

## Auditoria

Todas as ações sensíveis devem ser registradas:

- login
- criação de usuário
- criação de conta
- registro de transação
- alterações administrativas

Campos registrados:

```
user_id
action
entity
entity_id
timestamp
```

---

# 🏗️ Arquitetura Esperada

Estrutura recomendada:

```
src/main/java/com/company/finance

config/
security/
controller/
service/
repository/
domain/
dto/
mapper/
audit/
exception/
```

Descrição:

| Diretório  | Função                     |
| ---------- | -------------------------- |
| config     | configurações do Spring    |
| security   | autenticação e autorização |
| controller | endpoints HTTP             |
| service    | regras de negócio          |
| repository | acesso ao banco            |
| domain     | entidades e enums          |
| dto        | objetos de transporte      |
| mapper     | conversão DTO ↔ entidade   |
| audit      | auditoria                  |
| exception  | tratamento global          |

---

# 🗄️ Modelo de Dados

## User

```
id
name
email
password
role
active
created_at
```

---

## Account

```
id
user_id
name
active
created_at
```

---

## Transaction

```
id
account_id
type
amount
created_at
```

---

## AuditLog

```
id
user_id
action
entity_name
entity_id
created_at
```

---

# 🔐 Segurança

Implementar autenticação com:

- JWT
- BCrypt
- Authorization baseada em roles

Roles:

```
ROLE_USER
ROLE_ADMIN
```

---

# ⚙️ Banco de Dados

Requisitos:

- migrations versionadas com Flyway
- constraints bem definidas
- índices para consultas frequentes

Exemplo:

```
users.email UNIQUE
accounts.user_id INDEX
transactions.account_id INDEX
```

---

# 🧪 Testes

Projeto deve possuir:

### Testes unitários

Camada service.

### Testes de integração

Endpoints principais.

Uso recomendado:

- Testcontainers para subir PostgreSQL real.

---

# 🐳 Docker

Projeto deve possuir:

```
Dockerfile
docker-compose.yml
```

Serviços:

- aplicação
- PostgreSQL

---

# 📊 Observabilidade

Aplicação deve possuir:

- logs padronizados
- logs de erro estruturados
- correlação de requisições

---

# 📄 Documentação

Expor documentação com:

- OpenAPI
- Swagger UI

Endpoint:

```
/swagger-ui.html
```

---

# ⭐ Diferenciais (Opcional)

- Auditoria via AOP
- MapStruct para DTO mapping
- Rate limiting
- Caching com Redis
- CI pipeline

---

# 📦 Resultado Esperado

Uma API que demonstre:

- domínio do ecossistema Spring
- arquitetura backend madura
- segurança consistente
- boas práticas enterprise
- organização profissional de código

---

# 📚 Materiais Recomendados

Documentação oficial:

- Spring Boot
- Spring Security
- Spring Data JPA

---

# ⏱️ Prazo sugerido

30 a 50 dias.

---

# 💡 Ordem recomendada de desenvolvimento

1. Modelagem do banco
2. Entidades JPA
3. Repositories
4. Services
5. Segurança JWT
6. Controllers
7. Auditoria
8. Testes
9. Docker
10. Documentação
