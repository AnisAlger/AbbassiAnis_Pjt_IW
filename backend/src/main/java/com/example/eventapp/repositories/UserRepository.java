package com.example.eventapp.repositories;

import com.example.eventapp.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    // Nouvelle méthode pour récupérer tous les utilisateurs avec un rôle donné
    List<User> findByRole(String role);
}
