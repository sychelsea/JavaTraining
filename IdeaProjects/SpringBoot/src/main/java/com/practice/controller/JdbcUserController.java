package com.practice.controller;

import com.practice.domain.User;
import com.practice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v2/api")
public class JdbcUserController {
    private final UserService service;

    JdbcUserController(UserService userService) {
        this.service = userService;
    }

    // read UserId
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        return ResponseEntity.ok(service.getUser(id));
    }

    @PostMapping(value = "/user/{id}")
    public ResponseEntity<User> createUser(@PathVariable long id, @RequestBody User body) {
        body.setId(id);
        User user = service.createUser(body);       // UserAlreadyExistsException
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // delete UserId
    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable long id) {
        User user = service.deleteUser(id);
        return ResponseEntity.ok(user);

    }

    // Update UserId + new RequestBody user
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User body) {
        User user = service.updateUser(id, body);
        return ResponseEntity.ok(user);
    }
}
