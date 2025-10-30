package com.practice.controller;

import com.practice.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/v1/api")
public class UserController {
    private static Map<String, String> userDB = new HashMap<>();

    // read UserId
    @GetMapping("/user/{id}")
    public ResponseEntity<String> getUser(@PathVariable String id) {
        if (!userDB.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found: " + id);
        }
        return ResponseEntity.ok("User info for ID " + id + ": " + userDB.get(id));
    }

    // insert (RequestBody)
    @PostMapping("/user/{id}")
    public ResponseEntity<String> createUser(@PathVariable String id, @RequestBody String profileStr) {
        // Idempotency check
        if (userDB.containsKey(id)) {
            throw new UserAlreadyExistsException("User with ID " + id + " already exists");
            //return ResponseEntity.status(HttpStatus.CONFLICT)
            //        .body("User with ID " + id + " already exists");
        }

        userDB.put(id, profileStr);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully: " + profileStr);
    }

    // delete UserId
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        if (!userDB.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot delete. User not found: " + id);
        }
        userDB.remove(id);
        return ResponseEntity.ok("User deleted successfully"); // status - ok
    }

    // Update UserId + new RequestBody user
    @PutMapping("/user/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody String userJson) {
        if (!userDB.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot update. User not found: " + id);
        }
        userDB.put(id, userJson);
        return ResponseEntity.ok("User updated successfully: " + userJson);
    }
}
