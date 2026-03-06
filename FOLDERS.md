```bash
finance-api/
│
├── src/main/java/com/company/finance/
│   │
│   ├── FinanceApiApplication.java
│   │
│   ├── config/
│   │   ├── SecurityConfig.java          # Filter chain, CORS, regras de acesso
│   │   ├── JwtConfig.java               # Propriedades do JWT (secret, expiração)
│   │   ├── OpenApiConfig.java           # Configuração Swagger/OpenAPI
│   │   └── AuditConfig.java             # Beans de auditoria (AuditorAware, etc.)
│   │
│   ├── security/
│   │   ├── JwtService.java              # Gera e valida tokens
│   │   ├── JwtAuthFilter.java           # Intercepta requisições e valida JWT
│   │   ├── CustomUserDetailsService.java # Carrega usuário do banco pro Spring Security
│   │   └── SecurityUtils.java           # Helper: pega usuário logado do contexto
│   │
│   ├── domain/
│   │   ├── User.java                    # Entidade usuário
│   │   ├── Account.java                 # Entidade conta
│   │   ├── Transaction.java             # Entidade transação
│   │   ├── AuditLog.java                # Entidade log de auditoria
│   │   └── enums/
│   │       ├── Role.java                # ROLE_USER, ROLE_ADMIN
│   │       └── TransactionType.java     # CREDIT, DEBIT
│   │
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── AccountRepository.java
│   │   ├── TransactionRepository.java
│   │   └── AuditLogRepository.java
│   │
│   ├── dto/
│   │   ├── auth/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java       # Retorna access + refresh token
│   │   │   └── RefreshRequest.java
│   │   ├── user/
│   │   │   ├── UserResponse.java
│   │   │   └── UserSummaryResponse.java # Versão resumida pra listagem
│   │   ├── account/
│   │   │   ├── AccountRequest.java
│   │   │   └── AccountResponse.java
│   │   └── transaction/
│   │       ├── TransactionRequest.java
│   │       └── TransactionResponse.java
│   │
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   ├── AccountMapper.java
│   │   └── TransactionMapper.java
│   │
│   ├── service/
│   │   ├── AuthService.java             # Register, login, refresh
│   │   ├── UserService.java             # Listagem, deactivate
│   │   ├── AccountService.java          # CRUD de contas
│   │   └── TransactionService.java      # Crédito, débito, validação de saldo
│   │
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── AccountController.java
│   │   └── TransactionController.java
│   │
│   ├── audit/
│   │   ├── AuditService.java            # Salva logs no banco
│   │   └── AuditAspect.java             # AOP: intercepta métodos e chama AuditService
│   │
│   └── exception/
│       ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│       ├── BusinessException.java       # Exceção base do domínio
│       ├── InsufficientBalanceException.java
│       ├── ResourceNotFoundException.java
│       └── UnauthorizedException.java
│
├── src/main/resources/
│   ├── application.yml                  # Config principal
│   ├── application-dev.yml              # Overrides de desenvolvimento
│   └── db/migration/
│       ├── V1__create_users.sql
│       ├── V2__create_accounts.sql
│       ├── V3__create_transactions.sql
│       └── V4__create_audit_logs.sql
│
├── src/test/java/com/company/finance/
│   ├── service/
│   │   ├── AuthServiceTest.java
│   │   ├── AccountServiceTest.java
│   │   └── TransactionServiceTest.java  # Mais crítico — testa regras de saldo
│   └── controller/
│       ├── AuthControllerTest.java       # Testes de integração com Testcontainers
│       ├── AccountControllerTest.java
│       └── TransactionControllerTest.java
│
├── Dockerfile
├── docker-compose.yml
└── README.md
```
