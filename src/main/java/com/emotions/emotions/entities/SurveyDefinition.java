package com.emotions.emotions.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor 
public class SurveyDefinition {
    private List<SurveyQuestion> questions;
}
