package com.emotions.emotions.services;

import java.util.List;
import java.util.Optional;
import com.emotions.emotions.entities.SurveyTemplate;

public interface SurveyTemplateService {
    List<SurveyTemplate> findAll();
    Optional<SurveyTemplate> findById(Integer id);
    Optional<SurveyTemplate> findByEmotion(String emotion);
    SurveyTemplate save(SurveyTemplate surveyTemplate);
    void deleteById(Integer id);
}