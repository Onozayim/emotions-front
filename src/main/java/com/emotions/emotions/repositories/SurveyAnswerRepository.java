package com.emotions.emotions.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emotions.emotions.entities.SurveyAnswer;
import com.emotions.emotions.entities.SurveyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SurveyAnswerRepository
        extends JpaRepository<SurveyAnswer, Integer>, JpaSpecificationExecutor<SurveyAnswer> {

    Optional<SurveyAnswer> findByToken(String token);

    List<SurveyAnswer> findByEmail(String email);

    Optional<SurveyAnswer> findFirstByEmailAndStatus(String email, SurveyStatus status);

    @EntityGraph(attributePaths = { "survey" })
    List<SurveyAnswer> findTop50ByStatusOrderByCreatedAtAsc(SurveyStatus status);

    @Query("""
                SELECT sa
                FROM SurveyAnswer sa
                JOIN FETCH sa.survey
                WHERE sa.status = :status
                  AND sa.lastActivityAt < :before
                ORDER BY sa.lastActivityAt ASC
            """)
    List<SurveyAnswer> findPendingSurveysReadyToSend(
            @Param("status") SurveyStatus status,
            @Param("before") LocalDateTime before,
            Pageable pageable);

    @EntityGraph(attributePaths = { "survey" })
    List<SurveyAnswer> findTop50ByStatusAndSentAtBeforeOrderBySentAtAsc(
            SurveyStatus status,
            LocalDateTime before);
}