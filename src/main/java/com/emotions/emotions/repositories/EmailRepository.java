package com.emotions.emotions.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emotions.emotions.entities.Email;

public interface EmailRepository extends JpaRepository<Email, Long>, JpaSpecificationExecutor<Email> {
    @Query(value = """
            SELECT
                COALESCE(fixed_emotion, emotion) AS emotion,
                COUNT(*) AS count
            FROM emails
            WHERE from_email = :email
            GROUP BY
                COALESCE(fixed_emotion, emotion)
            """, nativeQuery = true)
    List<Object[]> getEmotionCountsByEmail(
            @Param("email") String email);
}
