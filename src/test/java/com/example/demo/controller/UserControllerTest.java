package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.Phone;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.PhoneRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PhoneRepository phoneRepository;
    @Autowired
    EmailRepository emailRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        accountRepository.deleteAll();
        phoneRepository.deleteAll();
        emailRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldGetUserTest() throws JSONException {
        var user1 = User.builder()
                .id(1L)
                .name("John")
                .birthDate(LocalDate.now())
                .password("$2a$10$reWROLTWMEwZCKVdOP/ceOeK9HppwY7XPfgaHZtDJ2mWf486Afzvm")
                .build();
        var user2 = User.builder()
                .id(2L)
                .name("Masha")
                .password("$2a$10$reWROLTWMEwZCKVdOP/ceOeK9HppwY7XPfgaHZtDJ2mWf486Afzvm")
                .birthDate(LocalDate.now())
                .build();

        List<User> users = List.of(
                user1, user2
        );
        userRepository.saveAll(users);
        List<Phone> phones = List.of(
                new Phone(1L, user1, "79995773254"),
                new Phone(2L, user2, "79995773777")
        );
        phoneRepository.saveAll(phones);

        var accounts = List.of(Account.builder()
                        .id(1L)
                        .balance(new BigDecimal(10000000))
                        .user(user1)
                        .build(),
                Account.builder()
                        .id(1L)
                        .balance(new BigDecimal(100000000))
                        .user(user2)
                        .build()
        );
        accountRepository.saveAll(accounts);

        var jsonObject = new JSONObject(given()
                .contentType(ContentType.JSON)
                .body(JwtRequest.builder()
                        .username("79995773777")
                        .password("12345678")
                        .build())
                .when()
                .post("login").getBody().prettyPrint());
        var token = jsonObject.getString("token");

        given()
                .contentType(ContentType.JSON)
                .header(new Header("Authorization", "Bearer " + token))
                .when()
                .get("/user/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("Masha"));
    }
}
