package com.practice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.model.User;
import com.practice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NewUserController.class)
@AutoConfigureMockMvc // addFilters = true
class NewUserControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    // 1) Without logging in: GET /v2/api/user/1 => re-directed to OAuth login
    @Test
    void getUser_anonymous_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/v2/api/user/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/oauth2/authorization/google"));
    }


    // 2) ROLE_USER: GET /v2/api/user/1 => 200 OK
    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void getUser_withRoleUser_shouldReturn200() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setPassword("x");
        u.setRole("USER");

        when(userService.getUser(1L)).thenReturn(u);

        mockMvc.perform(get("/v2/api/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("alice"));

        verify(userService).getUser(1L);
    }

    // 3) ROLE_USER: POST /v2/api/user => 403 Forbidden (201 in test)
    @Test
    @WithMockUser(username = "bob", roles = {"USER"})
    void createUser_withRoleUser_shouldReturn201() throws Exception {
        User body = new User();
        body.setUsername("newUser");
        body.setPassword("p");
        body.setRole("USER");

        User created = new User();
        created.setId(10L);
        created.setUsername("newUser");
        created.setPassword("ENC");
        created.setRole("USER");

        when(userService.createUser(any(User.class))).thenReturn(created);

        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/v2/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.username").value("newUser"));

        verify(userService).createUser(any(User.class));
    }


    // 4) ROLE_ADMIN: POST /v2/api/user => 201 Created
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_withRoleAdmin_shouldReturn201() throws Exception {
        User body = new User();
        body.setUsername("newUser");
        body.setPassword("p");
        body.setRole("USER");

        User created = new User();
        created.setId(10L);
        created.setUsername("newUser");
        created.setPassword("ENC");
        created.setRole("USER");

        when(userService.createUser(any(User.class))).thenReturn(created);

        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/v2/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.username").value("newUser"));

        verify(userService).createUser(any(User.class));
    }

    // 5) ROLE_USER:  PUT /v2/api/user/1 => 403 Forbidden
    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void updateUser_withRoleUser_shouldReturn403() throws Exception {
        User body = new User();
        body.setUsername("updated");
        body.setPassword("p");
        body.setRole("USER");

        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/v2/api/user/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateUser(anyLong(), any(User.class));
    }

    // 6) ROLE_ADMIN: DELETE /v2/api/user/1 => 200 OK
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_withRoleAdmin_shouldReturn200() throws Exception {
        User deleted = new User();
        deleted.setId(1L);
        deleted.setUsername("toDelete");
        deleted.setPassword("x");
        deleted.setRole("USER");

        when(userService.deleteUser(1L)).thenReturn(deleted);

        mockMvc.perform(delete("/v2/api/user/{id}", 1L)
                        .with(csrf()) )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(userService).deleteUser(1L);
    }
}
