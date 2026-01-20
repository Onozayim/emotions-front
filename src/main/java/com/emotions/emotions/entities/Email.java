package com.emotions.emotions.entities;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(name = "body", nullable = true)
    private String body;

    @Column(name = "subject", nullable = true)
    private String subject;
    
    @Column(name = "emotion", nullable = true)
    private String emotion;

    @Column(name = "from", nullable = false)
    private String from;
}
