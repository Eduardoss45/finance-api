**5. Auditoria**
- Criar `AuditService`.
- Criar `AuditAspect` ou inserir chamadas nos serviços.
- Persistir logs no banco.

**6. Documentação**
- Adicionar `springdoc-openapi`.
- Configurar `/swagger-ui.html`.

**7. Testes**
- Unitários para services críticos (principalmente `TransactionService`).
- Integração com Testcontainers (auth, accounts, transactions).

**8. Docker**
- Criar `Dockerfile` da API.
- Validar com `docker-compose`.
