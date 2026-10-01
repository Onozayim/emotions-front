package com.emotions.emotions.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor 
@AllArgsConstructor 
@Data 
public class EmotionSummary {
    private String emotion;
    private long count;
    private double percentage;
}