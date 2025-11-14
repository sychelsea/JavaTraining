package com.practice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.model.User;
import com.practice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NewUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class NewUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getUser_returnsUser_whenFound() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setPassword("secret");
        u.setRole("USER");

        when(userService.getUser(1L)).thenReturn(u);

        mockMvc.perform(get("/v2/api/user/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("alice"));

        verify(userService).getUser(1L);
    }

    @Test
    void createUser_returns201_andBody() throws Exception {
        User input = new User();
        input.setUsername("bob");
        input.setPassword("plain");
        input.setRole("ADMIN");

        User created = new User();
        created.setId(10L);
        created.setUsername("bob");
        created.setPassword("ENC");
        created.setRole("ADMIN");

        when(userService.createUser(any(User.class))).thenReturn(created);

        String json = objectMapper.writeValueAsString(input);

        mockMvc.perform(post("/v2/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.username").value("bob"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void deleteUser_returnsDeletedUser() throws Exception {
        User deleted = new User();
        deleted.setId(3L);
        deleted.setUsername("charlie");
        deleted.setPassword("x");
        deleted.setRole("USER");

        when(userService.deleteUser(3L)).thenReturn(deleted);

        mockMvc.perform(delete("/v2/api/user/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.username").value("charlie"));

        verify(userService).deleteUser(3L);
    }

    @Test
    void updateUser_returnsUpdatedUser() throws Exception {
        User body = new User();
        body.setUsername("newName");
        body.setPassword("newPass");
        body.setRole("ADMIN");

        User updated = new User();
        updated.setId(5L);
        updated.setUsername("newName");
        updated.setPassword("ENC");
        updated.setRole("ADMIN");

        when(userService.updateUser(eq(5L), any(User.class))).thenReturn(updated);

        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/v2/api/user/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.username").value("newName"));

        verify(userService).updateUser(eq(5L), any(User.class));
    }

    @Test
    void updateUserWithPessimisticLock_uses5000HoldMillis_andReturnsUser() throws Exception {
        User body = new User();
        body.setUsername("lockP");
        body.setPassword("p");
        body.setRole("USER");

        User updated = new User();
        updated.setId(7L);
        updated.setUsername("lockP");
        updated.setPassword("p");
        updated.setRole("USER");

        when(userService.updateUserWithPessimisticLock(eq(7L), any(User.class), eq(5000L)))
                .thenReturn(updated);

        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/v2/api/user/pl/{id}", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.username").value("lockP"));

        verify(userService).updateUserWithPessimisticLock(eq(7L),
                any(User.class),
                eq(5000L));
    }

    @Test
    void updateUserWithOptimisticLock_returnsUser() throws Exception {
        User body = new User();
        body.setUsername("lockO");
        body.setPassword("p");
        body.setRole("USER");

        User updated = new User();
        updated.setId(8L);
        updated.setUsername("lockO");
        updated.setPassword("p");
        updated.setRole("USER");

        when(userService.updateUserWithOptimisticLock(eq(8L), any(User.class)))
                .thenReturn(updated);

        String json = objectMapper.writeValueAsString(body);

        mockMvc.perform(put("/v2/api/user/ol/{id}", 8L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.username").value("lockO"));

        verify(userService).updateUserWithOptimisticLock(eq(8L), any(User.class));
    }
}
