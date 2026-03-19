package com.finances.finances_api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finances.finances_api.dto.auth.AuthResponse;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthAccountsTransactionsTest {

    private static final String JWT_SECRET = "uVdLxq8P1mFQKp1xQ9l5t5H8y8j5kM9c9r9zF3z1lW8=";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("finances_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("JWT_SECRET", () -> JWT_SECRET);
        registry.add("jwt.secret", () -> JWT_SECRET);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @LocalServerPort
    private int port;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpHeaders authHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    @Test
    void fullFlowAuthAccountsTransactions() throws Exception {
        TestRestTemplate restTemplate = new TestRestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        String email = "user_" + UUID.randomUUID() + "@test.com";
        String password = "Passw0rd!123";
        String name = "Test User";

        Map<String, Object> registerBody = Map.of(
                "name", name,
                "email", email,
                "password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> registerEntity = new HttpEntity<>(registerBody, headers);

        ResponseEntity<AuthResponse> registerResp = restTemplate.postForEntity(
                baseUrl() + "/auth/register", registerEntity, AuthResponse.class);

        assertThat(registerResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResp.getBody()).isNotNull();

        AuthResponse register = registerResp.getBody();
        String accessToken = register.getAccessToken();
        String refreshToken = register.getRefreshToken();
        UUID userId = register.getUserId();

        assertThat(accessToken).isNotBlank();
        assertThat(refreshToken).isNotBlank();
        assertThat(userId).isNotNull();

        Map<String, Object> loginBody = Map.of(
                "email", email,
                "password", password);
        ResponseEntity<AuthResponse> loginResp = restTemplate.postForEntity(
                baseUrl() + "/auth/login", new HttpEntity<>(loginBody, headers), AuthResponse.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody()).isNotNull();
        refreshToken = loginResp.getBody().getRefreshToken();

        Map<String, Object> refreshBody = Map.of("refreshToken", refreshToken);
        ResponseEntity<AuthResponse> refreshResp = restTemplate.postForEntity(
                baseUrl() + "/auth/refresh", new HttpEntity<>(refreshBody, headers), AuthResponse.class);
        assertThat(refreshResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> usersList = restTemplate.exchange(
                baseUrl() + "/users",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(accessToken)),
                String.class);
        assertThat(usersList.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> userById = restTemplate.exchange(
                baseUrl() + "/users/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(accessToken)),
                String.class);
        assertThat(userById.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> accountBody = Map.of("name", "Main Account");
        ResponseEntity<String> accountCreate = restTemplate.exchange(
                baseUrl() + "/accounts",
                HttpMethod.POST,
                new HttpEntity<>(accountBody, mergeHeaders(headers, authHeaders(accessToken))),
                String.class);
        assertThat(accountCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        @SuppressWarnings("unchecked")
        Map<String, Object> accountJson = mapper.readValue(accountCreate.getBody(), Map.class);
        String accountId = (String) accountJson.get("id");
        assertThat(accountId).isNotBlank();

        ResponseEntity<String> accountsList = restTemplate.exchange(
                baseUrl() + "/accounts?page=0&size=20",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(accessToken)),
                String.class);
        assertThat(accountsList.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> accountById = restTemplate.exchange(
                baseUrl() + "/accounts/" + accountId,
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(accessToken)),
                String.class);
        assertThat(accountById.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> creditBody = Map.of("type", "CREDIT", "amount", 100.00);
        ResponseEntity<String> creditResp = restTemplate.exchange(
                baseUrl() + "/accounts/" + accountId + "/transactions",
                HttpMethod.POST,
                new HttpEntity<>(creditBody, mergeHeaders(headers, authHeaders(accessToken))),
                String.class);
        assertThat(creditResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map<String, Object> debitBody = Map.of("type", "DEBIT", "amount", 40.00);
        ResponseEntity<String> debitResp = restTemplate.exchange(
                baseUrl() + "/accounts/" + accountId + "/transactions",
                HttpMethod.POST,
                new HttpEntity<>(debitBody, mergeHeaders(headers, authHeaders(accessToken))),
                String.class);
        assertThat(debitResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> txList = restTemplate.exchange(
                baseUrl() + "/accounts/" + accountId + "/transactions?page=0&size=20",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(accessToken)),
                String.class);
        assertThat(txList.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> deleteAccount = restTemplate.exchange(
                baseUrl() + "/accounts/" + accountId,
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders(accessToken)),
                Void.class);
        assertThat(deleteAccount.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private HttpHeaders mergeHeaders(HttpHeaders base, HttpHeaders auth) {
        HttpHeaders merged = new HttpHeaders();
        merged.putAll(base);
        merged.putAll(auth);
        return merged;
    }
}
