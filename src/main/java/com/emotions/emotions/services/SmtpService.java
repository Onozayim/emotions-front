package com.emotions.emotions.services;

import java.util.List;

import org.thymeleaf.context.Context;

import com.emotions.emotions.entities.EmailDetails;
import com.emotions.emotions.entities.EmotionSummary;
import com.emotions.emotions.entities.SurveyAnswer;

public interface SmtpService {
    public String sendSimplMail(EmailDetails details);
    public String sendHtmlEmail(EmailDetails emailDetails, Context context, String template);
    void sendSurvey(SurveyAnswer surveyAnswer);
    void sendEmotionSummary(SurveyAnswer surveyAnswer, List<EmotionSummary> summary,byte[] chart);
    void sendEmotionSummary(SurveyAnswer surveyAnswer);
}
