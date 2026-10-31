package com.emotions.emotions.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emotions.emotions.entities.Token;

public interface TokenRepository extends JpaRepository <Token, String> {
    @Query("SELECT t FROM Token t where t.email = :email and t.used = FALSE")
    Optional <Token> findPreviousToken(@Param("email") String email);
    
}
