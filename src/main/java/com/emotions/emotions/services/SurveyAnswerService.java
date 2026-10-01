package com.emotions.emotions.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.emotions.emotions.entities.EmotionSummarySnapshot;
import com.emotions.emotions.entities.SurveyAnswer;
import com.emotions.emotions.entities.SurveyStatus;

public interface SurveyAnswerService {
    List<SurveyAnswer> findAll();
    Optional<SurveyAnswer> findById(Integer id);
    Optional<SurveyAnswer> findByToken(String token);
    List<SurveyAnswer> findByEmail(String email);
    Optional<SurveyAnswer> findPendingByEmail(String email);
    SurveyAnswer save(SurveyAnswer surveyAnswer);
    SurveyAnswer updateStatus(Integer id, SurveyStatus status);
    void deleteById(Integer id);
    SurveyAnswer submitAnswer(String token, Map<String, String> answers);
    void sendPendingSurveys();
    void resendUnansweredSurveys();
    EmotionSummarySnapshot getEmotionSummary(String email);
}