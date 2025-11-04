
package com.practice.controller;

import com.practice.dao.cassandra.UserEventRepository;
import com.practice.model.UserEvent;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/api/event")
public class UserEventController {
    private final UserEventRepository repo;

    public UserEventController(UserEventRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public List<UserEvent> listEvents(@PathVariable Long id) {
        return repo.findByKeyUserId(id);
    }
}
