package com.practice.controller;

import com.practice.model.User;
import com.practice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v2/api")
public class NewUserController {
    private final UserService service;

    NewUserController(UserService userService) {
        this.service = userService;
    }

    // read UserId
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        return ResponseEntity.ok(service.getUser(id));
    }

    // create User
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/user")
    public ResponseEntity<User> createUser(@RequestBody User body) {
        User user = service.createUser(body);       // UserAlreadyExistsException
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // delete UserId
    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable long id) {
        User user = service.deleteUser(id);
        return ResponseEntity.ok(user);

    }

    // Update UserId + new RequestBody user
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User body) {
        User user = service.updateUser(id, body);
        return ResponseEntity.ok(user);
    }


    // ========= Update with locks ==========

    // Update pessimistic lock
    @PutMapping("/user/pl/{id}")
    public ResponseEntity<User> updateUserWithPessimisticLock(@PathVariable long id, @RequestBody User body) {
        User user = service.updateUserWithPessimisticLock(id, body, 5000);
        return ResponseEntity.ok(user);
    }

    // Update optimistic lock
    @PutMapping("/user/ol/{id}")
    public ResponseEntity<User> updateUserWithOptimisticLock(@PathVariable long id, @RequestBody User body) {
        User user = service.updateUserWithOptimisticLock(id, body);
        return ResponseEntity.ok(user);
    }
}
