Não automaticamente. **O Spring não valida DTOs por padrão.** Ele apenas faz **data binding** do JSON para o objeto. Validação só ocorre se você **ativar Bean Validation**.

---

# 1. O que o Spring faz por padrão

Quando um request chega:

```
HTTP JSON
↓
Jackson
↓
DTO
↓
Controller
```

O **Jackson** tenta mapear os campos.

Exemplo:

Request:

```json
{
  "email": "edu@email.com"
}
```

DTO:

```java
public record RegisterRequest(
    String name,
    String email,
    String password
) {}
```

Resultado:

```
name = null
email = "edu@email.com"
password = null
```

O Spring **não bloqueia** isso.

---

# 2. Para o Spring barrar requests você precisa usar validação

Isso é feito com **Bean Validation (Jakarta Validation)**.

Dependência normalmente já vem no Spring Boot:

```
spring-boot-starter-validation
```

---

# 3. DTO com validação

Exemplo correto:

```java
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank
    @Size(min = 3, max = 120)
    String name,

    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(min = 8)
    String password

) {}
```

---

# 4. Ativar validação no Controller

Você precisa usar `@Valid`.

```java
@PostMapping("/auth/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    service.register(request);
    return ResponseEntity.ok().build();
}
```

Fluxo agora:

```
HTTP Request
↓
Jackson converte JSON → DTO
↓
Bean Validation roda
↓
Se inválido → 400 Bad Request
↓
Se válido → Controller
```

---

# 5. Exemplo de erro automático

Request inválido:

```json
{
  "email": "not-an-email"
}
```

Resposta padrão do Spring:

```json
{
  "timestamp": "...",
  "status": 400,
  "errors": [
    "name must not be blank",
    "password must not be blank",
    "email must be a well-formed email address"
  ]
}
```

---

# 6. Outro comportamento importante

Campos **extras** no JSON são ignorados por padrão.

Exemplo:

```json
{
  "name": "Eduardo",
  "email": "edu@email.com",
  "password": "12345678",
  "role": "ADMIN"
}
```

DTO:

```
name
email
password
```

Resultado:

```
role → ignorado
```

Isso é comportamento do **Jackson**.

---

# 7. Se quiser barrar campos extras

Você pode configurar:

```java
@JsonIgnoreProperties(ignoreUnknown = false)
```

ou globalmente:

```
spring.jackson.deserialization.fail-on-unknown-properties=true
```

Agora o request acima retornaria:

```
400 Bad Request
Unknown field: role
```

---

# 8. Resumo

| Situação       | Comportamento padrão |
| -------------- | -------------------- |
| campo faltando | vira `null`          |
| campo inválido | aceito               |
| campo extra    | ignorado             |
| validação      | não ocorre           |

Para corrigir:

```
@NotBlank
@Email
@Size
@Valid
```

---

Se quiser, posso te mostrar também **como APIs Spring enterprise estruturam validação**, porque normalmente elas usam:

```
ControllerAdvice
ProblemDetails
ErrorResponse padronizado
```

Isso é **padrão em APIs corporativas** e aparece muito em entrevistas backend.
