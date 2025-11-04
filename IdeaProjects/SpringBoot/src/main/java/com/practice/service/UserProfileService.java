package com.practice.service;

import com.practice.dao.mangodb.UserProfileRepository;
import com.practice.dao.sql.JpaUserRepository;
import com.practice.model.User;
import com.practice.model.UserProfile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class UserProfileService {

    private final JpaUserRepository userRepo;
    private final UserProfileRepository profileRepo;

    public UserProfileService(JpaUserRepository userRepo, UserProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    public UserProfile upsertProfile(long userId, Map<String, Object> prefs) {
        // confirm the user id with sql database
        User u = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 2) write into mangodb
        UserProfile doc = profileRepo.findById(String.valueOf(u.getId())).orElseGet(UserProfile::new);
        doc.setId(String.valueOf(u.getId()));
        doc.setPreferences(prefs);
        doc.setUpdatedAt(Instant.now());
        return profileRepo.save(doc);
    }

    public UserProfile getProfile(long userId) {
        return profileRepo.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("No profile for user " + userId));
    }
}