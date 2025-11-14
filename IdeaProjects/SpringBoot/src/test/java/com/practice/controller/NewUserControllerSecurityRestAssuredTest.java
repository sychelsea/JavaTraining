package com.practice.controller;

import com.practice.model.User;
import com.practice.service.UserService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * RestAssured + MockMvc + Spring Security
 */
@WebMvcTest(controllers = NewUserController.class)
@AutoConfigureMockMvc
class NewUserControllerSecurityRestAssuredTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    /**
     * 匿名访问受保护接口：
     * /v2/api/user/1 需要认证
     * 在当前配置下会被重定向到 Google Login Page
     */
    @Test
    void anonymousGetUser_shouldRedirectToLogin() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/v2/api/user/{id}", 1L)
                .then()
                .statusCode(302)
                .header("Location", containsString("http://localhost/oauth2/authorization/google"));
    }

    /**
     * ROLE_USER: GET /v2/api/user/1
     * => 200 OK
     */
    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void getUser_withRoleUser_shouldReturn200() {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setPassword("ENC");
        u.setRole("USER");
        u.setEnabled(true);

        when(userService.getUser(1L)).thenReturn(u);

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

    /**
     * ROLE_ADMIN: CSRF token + POST /v2/api/user
     * => 被允许，返回 201 Created。
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_withAdmin_andCsrf_shouldReturn201() {
        Map<String, Object> requestBody = Map.of(
                "username", "newUser",
                "password", "p",
                "role", "USER",
                "enabled", true
        );

        User created = new User();
        created.setId(10L);
        created.setUsername("newUser");
        created.setPassword("ENC");
        created.setRole("USER");
        created.setEnabled(true);

        when(userService.createUser(any(User.class))).thenReturn(created);

        given()
                .auth().with(csrf()) // 🔐 加上 CSRF，这样不会被 CsrfFilter 拦截
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/api/user")
                .then()
                .statusCode(201)
                .body("id", equalTo(10))
                .body("username", equalTo("newUser"));

        verify(userService).createUser(any(User.class));
    }

    /**
     * ROLE_ADMIN + 带 CSRF 的 DELETE /v2/api/user/1
     * => 允许删除，返回 200 OK。
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_withAdmin_andCsrf_shouldReturn200() {
        User deleted = new User();
        deleted.setId(1L);
        deleted.setUsername("toDelete");
        deleted.setPassword("x");
        deleted.setRole("USER");
        deleted.setEnabled(true);

        when(userService.deleteUser(1L)).thenReturn(deleted);

        given()
                .auth().with(csrf())
                .when()
                .delete("/v2/api/user/{id}", 1L)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("username", equalTo("toDelete"));

        verify(userService).deleteUser(1L);
    }


    @Test
    @WithMockUser(username = "bob", roles = {"USER"})
    void getUser_withUserRole_again_shouldReturn200() {
        User u = new User();
        u.setId(2L);
        u.setUsername("bob");
        u.setPassword("ENC");
        u.setRole("USER");
        u.setEnabled(true);

        when(userService.getUser(2L)).thenReturn(u);

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/v2/api/user/{id}", 2L)
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("username", equalTo("bob"));

        verify(userService).getUser(2L);
    }
}
