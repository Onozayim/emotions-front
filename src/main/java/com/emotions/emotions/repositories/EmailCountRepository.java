package com.emotions.emotions.repositories;

import org.springframework.stereotype.Repository;

import com.emotions.emotions.entities.EmailCount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface EmailCountRepository extends JpaRepository<EmailCount, Long> {
    @Query(value = "SELECT * FROM emails_count ORDER BY id DESC LIMIT 1", nativeQuery = true)
    EmailCount getLastRecord();

}
