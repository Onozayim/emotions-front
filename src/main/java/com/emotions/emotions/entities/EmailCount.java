package com.emotions.emotions.entities;

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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
}
