package com.emotions.emotions.entities;

import java.time.LocalDate;
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
@Table(name = "emails_count")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EnableJpaAuditing
public class EmailCount {

    public EmailCount(Long joy, Long sadness, Long anger, Long fear, Long love, Long surprise) {
        this.joy = joy != null ? joy.intValue() : 0;
        this.sadness = sadness != null ? sadness.intValue() : 0;
        this.anger = anger != null ? anger.intValue() : 0;
        this.fear = fear != null ? fear.intValue() : 0;
        this.love = love != null ? love.intValue() : 0;
        this.surprise = surprise != null ? surprise.intValue() : 0;
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

    @Column(nullable = false, name = "love")
    private int love = 0;

    @Column(nullable = false, name = "surprise")
    private int surprise = 0;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at", nullable = false)
    private Date createdAt;
}
