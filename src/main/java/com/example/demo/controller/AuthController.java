package com.example.demo.controller;

import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.model.Client;
import com.example.demo.repository.ClientRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClientRepository clientRepository;

    // SIGN UP - Inscription d'un nouvel utilisateur ou client
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> payload, BindingResult result) {
        String type = (String) payload.get("type");
        if (type == null) return ResponseEntity.badRequest().body("Type (user/client) requis");
        if (type.equals("user")) {
            User user = new User();
            user.setUsername((String) payload.get("username"));
            user.setPassword((String) payload.get("password"));
            user.setEmail((String) payload.get("email"));
            user.setPhone((String) payload.get("phone"));
            // Validation simplifiée ici
            if (userService.findByUsername(user.getUsername()).isPresent())
                return ResponseEntity.badRequest().body("Username already exists");
            if (userService.findByEmail(user.getEmail()).isPresent())
                return ResponseEntity.badRequest().body("Email already exists");
            User registeredUser = userService.register(user);
            User safeUser = new User();
            safeUser.setId(registeredUser.getId());
            safeUser.setUsername(registeredUser.getUsername());
            safeUser.setEmail(registeredUser.getEmail());
            safeUser.setPhone(registeredUser.getPhone());
            return ResponseEntity.status(HttpStatus.CREATED).body(safeUser);
        } else if (type.equals("client")) {
            Client client = new Client();
            client.setNom((String) payload.get("nom"));
            client.setAdresse((String) payload.get("adresse"));
            client.setNomSociete((String) payload.get("nomSociete"));
            client.setNumTel((String) payload.get("numTel"));
            client.setEmail((String) payload.get("email"));
            client.setPassword((String) payload.get("password"));
            // Validation simplifiée ici
            if (clientRepository.findByEmail(client.getEmail()).isPresent())
                return ResponseEntity.badRequest().body("Email already exists");
            if (clientRepository.findByNom(client.getNom()).isPresent())
                return ResponseEntity.badRequest().body("Nom already exists");
            Client saved = clientRepository.save(client);
            Client safeClient = new Client();
            safeClient.setId(saved.getId());
            safeClient.setNom(saved.getNom());
            safeClient.setAdresse(saved.getAdresse());
            safeClient.setNomSociete(saved.getNomSociete());
            safeClient.setNumTel(saved.getNumTel());
            safeClient.setEmail(saved.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(safeClient);
        } else {
            return ResponseEntity.badRequest().body("Type doit être 'user' ou 'client'");
        }
    }

    // SIGN IN - Connexion d'un utilisateur ou client
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, Object> payload, HttpSession session) {
        String type = (String) payload.get("type");
        if (type == null) return ResponseEntity.badRequest().body("Type (user/client) requis");
        if (type.equals("user")) {
            String username = (String) payload.get("username");
            String password = (String) payload.get("password");
            Optional<User> existingUser = userService.findByUsername(username);
            if (existingUser.isPresent() && userService.checkPassword(password, existingUser.get().getPassword())) {
                session.setMaxInactiveInterval(120);
                session.setAttribute("user", existingUser.get());
                User safeUser = new User();
                safeUser.setId(existingUser.get().getId());
                safeUser.setUsername(existingUser.get().getUsername());
                safeUser.setEmail(existingUser.get().getEmail());
                safeUser.setPhone(existingUser.get().getPhone());
                return ResponseEntity.ok(Map.of("type", "user", "user", safeUser));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } else if (type.equals("client")) {
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            Optional<Client> existingClient = clientRepository.findByEmail(email);
            if (existingClient.isPresent() && password.equals(existingClient.get().getPassword())) {
                session.setMaxInactiveInterval(120);
                session.setAttribute("client", existingClient.get());
                Client safeClient = new Client();
                safeClient.setId(existingClient.get().getId());
                safeClient.setNom(existingClient.get().getNom());
                safeClient.setAdresse(existingClient.get().getAdresse());
                safeClient.setNomSociete(existingClient.get().getNomSociete());
                safeClient.setNumTel(existingClient.get().getNumTel());
                safeClient.setEmail(existingClient.get().getEmail());
                return ResponseEntity.ok(Map.of("type", "client", "client", safeClient));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } else {
            return ResponseEntity.badRequest().body("Type doit être 'user' ou 'client'");
        }
    }

    // LOGIN - Vérification des coordonnées pour accéder à l'interface
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpSession session) {
        // Vérifier si l'utilisateur est déjà connecté
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser != null) {
            // L'utilisateur est déjà connecté, retourner ses infos
            User safeUser = new User();
            safeUser.setId(sessionUser.getId());
            safeUser.setUsername(sessionUser.getUsername());
            safeUser.setEmail(sessionUser.getEmail());
            safeUser.setPhone(sessionUser.getPhone());

            return ResponseEntity.ok(safeUser);
        }

        // Sinon, vérifier les coordonnées
        Optional<User> existingUser = userService.findByUsername(user.getUsername());

        if (existingUser.isPresent()
                && userService.checkPassword(user.getPassword(), existingUser.get().getPassword())) {

            // Créer la session
            session.setMaxInactiveInterval(120);
            session.setAttribute("user", existingUser.get());

            // Retourner l'utilisateur sans le mot de passe
            User safeUser = new User();
            safeUser.setId(existingUser.get().getId());
            safeUser.setUsername(existingUser.get().getUsername());
            safeUser.setEmail(existingUser.get().getEmail());
            safeUser.setPhone(existingUser.get().getPhone());

            return ResponseEntity.ok(safeUser);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    // LOGOUT - Déconnexion
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    // GET CURRENT USER - Récupérer l'utilisateur connecté
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // Retourner l'utilisateur sans le mot de passe
            User safeUser = new User();
            safeUser.setId(user.getId());
            safeUser.setUsername(user.getUsername());
            safeUser.setEmail(user.getEmail());
            safeUser.setPhone(user.getPhone());

            return ResponseEntity.ok(safeUser);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id,
                                        @Valid @RequestBody User updatedUser,
                                        BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Optional<User> updated = userService.updateUser(id, updatedUser);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok("User deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
