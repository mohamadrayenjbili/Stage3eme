//expose l'api
package com.example.demo.controller;

import com.example.demo.model.Test;
import com.example.demo.repository.TestRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final TestRepository repository;

    public TestController(TestRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String hello() {
        return "Backend connected!";
    }

    @PostMapping
    public Test saveMessage(@RequestBody Test test) {
        return repository.save(test); // Sauvegarde en base H2
    }
}