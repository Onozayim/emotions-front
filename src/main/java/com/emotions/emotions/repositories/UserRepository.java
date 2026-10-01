package com.emotions.emotions.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emotions.emotions.entities.User;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByEmail(String email);
}
