package com.emotions.emotions.entities;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "surveys_answers", uniqueConstraints = {
        @UniqueConstraint(name = "uq_surveys_answers_token", columnNames = "token")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@EnableJpaAuditing
public class SurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyTemplate survey;

    @Column(name = "token", nullable = false, unique = true, length = 40)
    private String token;

    @Lob
    @Column(name = "answers", columnDefinition = "LONGTEXT")
    private String answers;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private SurveyStatus status = SurveyStatus.PENDING;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Lob
    @Column(name = "template_snapshot", columnDefinition = "LONGTEXT")
    private String templateSnapshot;

    @Column(name = "last_activity_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime lastActivityAt;

    @Lob
    @Column(name = "emotion_summary_snapshot", columnDefinition = "LONGTEXT")
    private String emotionSummarySnapshot;
}