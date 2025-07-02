package com.example.demo.repository;

import com.example.demo.model.Test;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestRepository extends MongoRepository<Test, String> {
    // Tu peux ajouter des méthodes personnalisées ici
}
