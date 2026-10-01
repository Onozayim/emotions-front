package com.emotions.emotions.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table (name = "surveys_templates")
@AllArgsConstructor 
@NoArgsConstructor 
@Data 
@EnableJpaAuditing 
public class SurveyTemplate {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (
        name = "subject",
        nullable = false,
        length = 100
    )
    private String subject;

    @Column(
        name = "message",
        nullable = false,
        length = 255
    )
    private String message;

    @Lob 
    @Column(
        name = "template",
        nullable = false,
        columnDefinition = "LONGTEXT"
    )
    private String template;

    @Column(
        name = "emotion",
        nullable = false,
        length = 15
    )
    private String emotion;

    @Column(
        name = "created_at",
        nullable = false,
        insertable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    @OneToMany (
        mappedBy = "survey",
        fetch = FetchType.LAZY
    )
    private List<SurveyAnswer> answers = new ArrayList<>();
}