package com.practice.controller;

import com.practice.model.User;
import com.practice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@WebMvcTest(controllers = NewUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class NewUserControllerRestAssuredTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        // tell RestAssured to use MockMvc
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void getUser_shouldReturnUser_whenFound() {
        // mocked return value
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setPassword("ENC");
        u.setRole("USER");
        u.setEnabled(true);

        when(userService.getUser(1L)).thenReturn(u);

        // Real HTTP request
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/v2/api/user/{id}", 1L)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("username", equalTo("alice"))
                .body("role", equalTo("USER"));

        verify(userService).getUser(1L);
    }

    @Test
    void createUser_shouldReturn201_andBody() {
        // Request body (User)
        Map<String, Object> requestBody = Map.of(
                "username", "bob",
                "password", "plain",
                "role", "ADMIN",
                "enabled", true
        );

        User created = new User();
        created.setId(10L);
        created.setUsername("bob");
        created.setPassword("ENC");
        created.setRole("ADMIN");
        created.setEnabled(true);

        when(userService.createUser(any(User.class))).thenReturn(created);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/api/user")
                .then()
                .statusCode(201)
                .body("id", equalTo(10))
                .body("username", equalTo("bob"))
                .body("role", equalTo("ADMIN"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void deleteUser_shouldReturnDeletedUser() {
        User deleted = new User();
        deleted.setId(3L);
        deleted.setUsername("charlie");
        deleted.setPassword("x");
        deleted.setRole("USER");
        deleted.setEnabled(true);

        when(userService.deleteUser(3L)).thenReturn(deleted);

        given()
                .accept(ContentType.JSON)
                .when()
                .delete("/v2/api/user/{id}", 3L)
                .then()
                .statusCode(200)
                .body("id", equalTo(3))
                .body("username", equalTo("charlie"));

        verify(userService).deleteUser(3L);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        Map<String, Object> requestBody = Map.of(
                "username", "newName",
                "password", "newPass",
                "role", "ADMIN",
                "enabled", true
        );

        User updated = new User();
        updated.setId(5L);
        updated.setUsername("newName");
        updated.setPassword("ENC");
        updated.setRole("ADMIN");
        updated.setEnabled(true);

        when(userService.updateUser(eq(5L), any(User.class))).thenReturn(updated);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/v2/api/user/{id}", 5L)
                .then()
                .statusCode(200)
                .body("id", equalTo(5))
                .body("username", equalTo("newName"))
                .body("role", equalTo("ADMIN"));

        verify(userService).updateUser(eq(5L), any(User.class));
    }

    @Test
    void updateUserWithPessimisticLock_shouldCallServiceWith5000HoldMillis() {
        Map<String, Object> requestBody = Map.of(
                "username", "lockP",
                "password", "p",
                "role", "USER",
                "enabled", true
        );

        User updated = new User();
        updated.setId(7L);
        updated.setUsername("lockP");
        updated.setPassword("p");
        updated.setRole("USER");
        updated.setEnabled(true);

        when(userService.updateUserWithPessimisticLock(eq(7L), any(User.class), eq(5000L)))
                .thenReturn(updated);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/v2/api/user/pl/{id}", 7L)
                .then()
                .statusCode(200)
                .body("id", equalTo(7))
                .body("username", equalTo("lockP"));

        verify(userService).updateUserWithPessimisticLock(eq(7L), any(User.class), eq(5000L));
    }

    @Test
    void updateUserWithOptimisticLock_shouldReturnUser() {
        Map<String, Object> requestBody = Map.of(
                "username", "lockO",
                "password", "p",
                "role", "USER",
                "enabled", true
        );

        User updated = new User();
        updated.setId(8L);
        updated.setUsername("lockO");
        updated.setPassword("p");
        updated.setRole("USER");
        updated.setEnabled(true);

        when(userService.updateUserWithOptimisticLock(eq(8L), any(User.class)))
                .thenReturn(updated);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/v2/api/user/ol/{id}", 8L)
                .then()
                .statusCode(200)
                .body("id", equalTo(8))
                .body("username", equalTo("lockO"));

        verify(userService).updateUserWithOptimisticLock(eq(8L), any(User.class));
    }
}
