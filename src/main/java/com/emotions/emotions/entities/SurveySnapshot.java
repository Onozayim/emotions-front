package com.emotions.emotions.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class SurveySnapshot {
    private String subject;
    private String message;
    private String emotion;
    private List<SurveyQuestion> questions;
}
