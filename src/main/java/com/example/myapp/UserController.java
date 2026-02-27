package com.example.myapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/users")
public class UserController {

    private final List<User> users = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong(1);

    // package-private helper used by tests to reset state
    void reset() {
        users.clear();
        sequence.set(1);
    }

    @GetMapping
    public List<User> getAll() {
        return users;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (user.getId() == null) {
            user.setId(sequence.getAndIncrement());
        }
        users.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        synchronized (users) {
            return users.stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
    }
}
