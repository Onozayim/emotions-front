package com.emotions.emotions.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class SurveyQuestionAnswerDto {
    private String id;
    private String type;
    private String label;
    private Object answer;
}
