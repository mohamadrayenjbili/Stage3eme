package com.example.demo.repository;

import com.example.demo.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ClientRepository extends MongoRepository<Client, String> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByNom(String nom);
} 