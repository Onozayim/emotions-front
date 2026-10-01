package com.emotions.emotions.Scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.emotions.emotions.services.SurveyAnswerService;

@Component
public class SurveyScheduler {

    private final SurveyAnswerService surveyAnswerService;

    public SurveyScheduler(
            SurveyAnswerService surveyAnswerService
    ) {
        this.surveyAnswerService = surveyAnswerService;
    }

    @Scheduled(
        cron = "${survey.scheduler.cron}",
        zone = "${survey.scheduler.zone}"
    )
    public void sendPendingSurveys() {

        surveyAnswerService.sendPendingSurveys();
        surveyAnswerService.resendUnansweredSurveys();
    }
}
