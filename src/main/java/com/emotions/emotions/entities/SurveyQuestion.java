package com.emotions.emotions.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor 
@NoArgsConstructor 
@Data 
public class SurveyQuestion {
    private String id;
    private String type;
    private String label;

    private boolean required;

    private Integer min;
    private Integer max;

    private List<String> options;

    private Integer maxLength;
}
