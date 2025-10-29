package com.practice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/v1/user")
public class UserController {
    private static Map<String, String> userDB = new HashMap<>();

    // read UserId
    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable String id) {
        if (!userDB.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found: " + id);
        }
        return ResponseEntity.ok("User info for ID " + id + ": " + userDB.get(id));
    }

    // insert (RequestBody)
    @PostMapping("/{id}")
    public ResponseEntity<String> createUser(@PathVariable String id, @RequestBody String profileStr) {
        // ✅ Idempotency check: 如果已存在则返回 409 Conflict
        if (userDB.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with ID " + id + " already exists");
        }

        userDB.put(id, profileStr);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully: " + profileStr);
    }

    // delete UserId
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        if (!userDB.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot delete. User not found: " + id);
        }
        userDB.remove(id);
        return ResponseEntity.ok("User deleted successfully"); // status - ok
    }

    // Update UserId + new RequestBody user
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody String userJson) {
        if (!userDB.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cannot update. User not found: " + id);
        }
        userDB.put(id, userJson);
        return ResponseEntity.ok("User updated successfully: " + userJson);
    }
}
