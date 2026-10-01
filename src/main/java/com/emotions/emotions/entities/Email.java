package com.emotions.emotions.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "emails")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EnableJpaAuditing
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "body", nullable = true)
    private String body;

    @Column(name = "subject", nullable = true)
    private String subject;
    
    @Column(name = "emotion", nullable = true)
    private String emotion;

    @Column(name = "from_email", nullable = false)
    private String from;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "secondary_emotion", nullable = true)
    private String secondaryEmotion;

    @Column(name = "compound_emotion", nullable = true)
    private String compoundEmotion;

    @Column(name = "fixed_emotion", nullable = true)
    private String fixedEmotion;

    @Column(name = "fixed_secondary_emotion", nullable = true)
    private String fixedSecondaryEmotion;
    
    @Column(name = "fixed_compound_emotion", nullable = true)
    private String fixedCompoundEmotion;
}
