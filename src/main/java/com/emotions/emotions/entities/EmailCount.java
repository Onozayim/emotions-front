package com.emotions.emotions.entities;

import java.time.LocalDate;

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
@Table(name = "emails_count")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EnableJpaAuditing
public class EmailCount {

    public EmailCount(Long joy, Long sadness, Long anger, Long fear, Long love, Long disgust) {
        this.joy = joy != null ? joy.intValue() : 0;
        this.sadness = sadness != null ? sadness.intValue() : 0;
        this.anger = anger != null ? anger.intValue() : 0;
        this.fear = fear != null ? fear.intValue() : 0;
        this.disgust = disgust != null ? disgust.intValue() : 0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    
    @Column(nullable = false, name = "joy")
    private int joy = 0;

    @Column(nullable = false, name = "sadness")
    private int sadness = 0;

    @Column(nullable = false, name = "anger")
    private int anger = 0;

    @Column(nullable = false, name = "fear")
    private int fear = 0;

    @Column(nullable = false, name = "disgust")
    private int disgust = 0;

    @Column(nullable = false, name = "surprise")
    private int surprise = 0;

    @Column(nullable = false, name = "nostalgia")
    private int nostalgia = 0;

    @Column(nullable = false, name = "intrigue")
    private int intrigue = 0;

    @Column(nullable = false, name = "justice")
    private int justice = 0;

    @Column(nullable = false, name = "contempt")
    private int contempt = 0;

    @Column(nullable = false, name = "anxiety")
    private int anxiety = 0;

    @Column(nullable = false, name = "betrayal")
    private int betrayal = 0;

    @Column(nullable = false, name = "repulsion")
    private int repulsion = 0;

    @Column(nullable = false, name = "aversion")
    private int aversion = 0;

    @Column(nullable = false, name = "hate")
    private int hate = 0;

    @Column(nullable = false, name = "sec_joy")
    private int sec_joy = 0;

    @Column(nullable = false, name = "sec_sadness")
    private int sec_sadness = 0;

    @Column(nullable = false, name = "sec_anger")
    private int sec_anger = 0;

    @Column(nullable = false, name = "sec_fear")
    private int sec_fear = 0;

    @Column(nullable = false, name = "sec_disgust")
    private int sec_disgust = 0;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at", nullable = false)
    private LocalDate createdAt;
}
