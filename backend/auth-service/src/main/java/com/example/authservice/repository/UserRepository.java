package com.example.authservice.repository;

import com.example.authservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    List<User> findByRole(String role);
}
