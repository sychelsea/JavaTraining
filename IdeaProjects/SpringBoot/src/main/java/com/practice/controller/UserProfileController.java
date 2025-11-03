package com.practice.controller;


import com.practice.model.UserProfile;
import com.practice.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {
    private final UserProfileService svc;

    public UserProfileController(UserProfileService svc) {
        this.svc = svc;
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> upsert(@PathVariable long userId,
                                              @RequestBody Map<String,Object> prefs) {
        return ResponseEntity.ok(svc.upsertProfile(userId, prefs));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> get(@PathVariable long userId) {
        return ResponseEntity.ok(svc.getProfile(userId));
    }
}
