package com.example.demo.controller;

import java.util.Optional;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        // Vérification des erreurs de validation
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        // Vérifier si username existe déjà
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Enregistrer l'utilisateur (penser à hacher le mot de passe dans le service)
        userService.register(user);

        return ResponseEntity.ok("User registered");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpSession session) {
        Optional<User> existingUser = userService.findByUsername(user.getUsername());

        // Vérifier que l'utilisateur existe et que le mot de passe correspond
        if (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword())) {

            // Durée de session = 2 minutes (120 secondes)
            session.setMaxInactiveInterval(120);

            session.setAttribute("user", existingUser.get());
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
