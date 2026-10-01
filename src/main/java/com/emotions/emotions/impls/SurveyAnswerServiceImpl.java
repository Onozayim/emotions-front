package com.emotions.emotions.impls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emotions.emotions.entities.EmotionSummary;
import com.emotions.emotions.entities.EmotionSummarySnapshot;
import com.emotions.emotions.entities.SurveyAnswer;
import com.emotions.emotions.entities.SurveyDefinition;
import com.emotions.emotions.entities.SurveyQuestion;
import com.emotions.emotions.entities.SurveySnapshot;
import com.emotions.emotions.entities.SurveyStatus;
import com.emotions.emotions.entities.SurveyTemplate;
import com.emotions.emotions.repositories.EmailRepository;
import com.emotions.emotions.repositories.SurveyAnswerRepository;
import com.emotions.emotions.services.SmtpService;
import com.emotions.emotions.services.SurveyAnswerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SurveyAnswerServiceImpl implements SurveyAnswerService {

    private final ObjectMapper objectMapper;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SmtpService smtpService;
    private final EmailRepository emailRepository;

    @Value("${survey.resend-days:15}")
    private long resendDays;

    public SurveyAnswerServiceImpl(
            SurveyAnswerRepository surveyAnswerRepository,
            ObjectMapper objectMapper,
            SmtpService smtpService,
            EmailRepository emailRepository) {
        this.surveyAnswerRepository = surveyAnswerRepository;
        this.objectMapper = objectMapper;
        this.smtpService = smtpService;
        this.emailRepository = emailRepository;
    }

    @Override
    public List<SurveyAnswer> findAll() {
        return surveyAnswerRepository.findAll();
    }

    @Override
    public Optional<SurveyAnswer> findById(Integer id) {
        return surveyAnswerRepository.findById(id);
    }

    @Override
    public Optional<SurveyAnswer> findByToken(String token) {
        return surveyAnswerRepository.findByToken(token);
    }

    @Override
    public List<SurveyAnswer> findByEmail(String email) {
        return surveyAnswerRepository.findByEmail(email);
    }

    @Override
    public Optional<SurveyAnswer> findPendingByEmail(
            String email) {
        return surveyAnswerRepository
                .findFirstByEmailAndStatus(
                        email,
                        SurveyStatus.PENDING);
    }

    @Override
    public SurveyAnswer save(
            SurveyAnswer surveyAnswer) {
        return surveyAnswerRepository.save(surveyAnswer);
    }

    @Override
    @Transactional
    public SurveyAnswer updateStatus(
            Integer id,
            SurveyStatus status) {
        SurveyAnswer surveyAnswer = surveyAnswerRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Survey answer not found: " + id));

        surveyAnswer.setStatus(status);

        return surveyAnswerRepository.save(surveyAnswer);
    }

    @Override
    public void deleteById(Integer id) {
        surveyAnswerRepository.deleteById(id);
    }

    @Override
    public SurveyAnswer submitAnswer(String token, Map<String, String> submittedAnswers) {
        SurveyAnswer surveyAnswer = surveyAnswerRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        if (surveyAnswer.getStatus() == SurveyStatus.ANSWERED)
            throw new IllegalStateException("Survey has already been answered");

        if (surveyAnswer.getStatus() == SurveyStatus.EXPIRED)
            throw new IllegalStateException("Survey has expired");

        SurveyTemplate surveyTemplate = surveyAnswer.getSurvey();
        String templateJson = surveyTemplate.getTemplate();

        try {
            SurveyDefinition definition = objectMapper.readValue(templateJson, SurveyDefinition.class);

            Map<String, Object> validatedAnswers = validateAnswers(definition, submittedAnswers);
            String answersJson = objectMapper.writeValueAsString(validatedAnswers);

            SurveySnapshot snapshot = new SurveySnapshot(
                    surveyTemplate.getSubject(),
                    surveyTemplate.getMessage(),
                    surveyTemplate.getEmotion(),
                    definition.getQuestions());

            String snapshotJson = objectMapper.writeValueAsString(snapshot);

            surveyAnswer.setAnswers(answersJson);
            surveyAnswer.setTemplateSnapshot(snapshotJson);
            surveyAnswer.setRespondedAt(LocalDateTime.now());
            surveyAnswer.setStatus(SurveyStatus.ANSWERED);

            EmotionSummarySnapshot emotionSummarySnapshot = this.getEmotionSummary(surveyAnswer.getEmail());

            surveyAnswer.setEmotionSummarySnapshot(
                    objectMapper.writeValueAsString(emotionSummarySnapshot));

            SurveyAnswer saved = surveyAnswerRepository.save(surveyAnswer);

            smtpService.sendEmotionSummary(saved);

            return saved;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid survey template", e);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Survey was answered, but emotion summary email could not be sent");
        }
    }

    private Map<String, Object> validateAnswers(SurveyDefinition definition, Map<String, String> submittedAnswers) {
        Map<String, Object> validatedAnswers = new LinkedHashMap<>();

        if (definition.getQuestions() == null)
            throw new IllegalStateException("Survey contains no questions");

        for (SurveyQuestion question : definition.getQuestions()) {

            String value = submittedAnswers.get(question.getId());

            if (question.isRequired() && (value == null || value.isBlank()))
                throw new IllegalArgumentException("Question is required: " + question.getLabel());

            if (value == null || value.isBlank())
                continue;

            switch (question.getType()) {

                case "rating" ->
                    validateRating(question, value, validatedAnswers);

                case "textarea" ->
                    validateTextarea(question, value, validatedAnswers);

                case "multiple_choice" ->
                    validateMultipleChoice(question, value, validatedAnswers);

                default ->
                    throw new IllegalArgumentException("Unsupported question type: " + question.getType());
            }
        }

        validateUnknownQuestions(definition, submittedAnswers);

        return validatedAnswers;
    }

    private void validateRating(SurveyQuestion question, String value, Map<String, Object> validatedAnswers) {
        int rating;
        try {
            rating = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid rating for question: " + question.getLabel());
        }

        if (question.getMin() == null || question.getMax() == null) {
            throw new IllegalStateException(
                    "Rating question does not define min/max: "
                            + question.getId());
        }

        if (rating < question.getMin() || rating > question.getMax()) {
            throw new IllegalArgumentException(
                    "Rating for '"
                            + question.getLabel()
                            + "' must be between "
                            + question.getMin()
                            + " and "
                            + question.getMax());
        }

        validatedAnswers.put(question.getId(), rating);
    }

    private void validateMultipleChoice(SurveyQuestion question, String value, Map<String, Object> validatedAnswers) {

        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            throw new IllegalStateException(
                    "Multiple choice question has no options: "
                            + question.getId());
        }

        if (!question.getOptions().contains(value)) {
            throw new IllegalArgumentException(
                    "Invalid option for question: "
                            + question.getLabel());
        }

        validatedAnswers.put(question.getId(), value);
    }

    private void validateTextarea(SurveyQuestion question, String value, Map<String, Object> validatedAnswers) {
        String sanitizedValue = value.trim();

        if (question.getMaxLength() != null && sanitizedValue.length() > question.getMaxLength()) {
            throw new IllegalArgumentException(
                    "Answer for '"
                            + question.getLabel()
                            + "' cannot exceed "
                            + question.getMaxLength()
                            + " characters");
        }

        validatedAnswers.put(question.getId(), sanitizedValue);
    }

    private void validateUnknownQuestions(SurveyDefinition definition, Map<String, String> submittedAnswers) {
        Set<String> validQuestionIds = definition
                .getQuestions()
                .stream()
                .map(SurveyQuestion::getId)
                .collect(Collectors.toSet());

        for (String submittedId : submittedAnswers.keySet()) {
            if (!validQuestionIds.contains(submittedId)) {
                throw new IllegalArgumentException(
                        "Unknown survey question: "
                                + submittedId);
            }
        }
    }

    @Override
    @Transactional
    public void sendPendingSurveys() {
        List<SurveyAnswer> pendingSurveys = surveyAnswerRepository
                .findTop50ByStatusOrderByCreatedAtAsc(
                        SurveyStatus.PENDING);

        for (SurveyAnswer survey : pendingSurveys) {

            try {
                smtpService.sendSurvey(survey);

                survey.setStatus(SurveyStatus.SENT);
                survey.setSentAt(LocalDateTime.now());

                // surveyAnswerRepository.save(survey);

            } catch (Exception ex) {
                System.out.println("Failed to send survey " + survey.getId());
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void resendUnansweredSurveys() {

        LocalDateTime resendBefore = LocalDateTime.now().minusDays(resendDays);

        List<SurveyAnswer> surveys = surveyAnswerRepository
                .findTop50ByStatusAndSentAtBeforeOrderBySentAtAsc(
                        SurveyStatus.SENT,
                        resendBefore);

        for (SurveyAnswer survey : surveys) {

            try {

                smtpService.sendSurvey(survey);
                survey.setSentAt(LocalDateTime.now());

            } catch (Exception ex) {

                System.out.println("Failed to resend survey " + survey.getId());
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public EmotionSummarySnapshot getEmotionSummary(
            String email) {

        List<Object[]> rows = emailRepository.getEmotionCountsByEmail(email);

        long total = rows.stream()
                .mapToLong(
                        row -> ((Number) row[1]).longValue())
                .sum();

        List<EmotionSummary> emotions = rows.stream()
                .map(row -> {

                    String emotion = (String) row[0];

                    long count = ((Number) row[1]).longValue();

                    double percentage = total == 0
                            ? 0
                            : (count * 100.0) / total;

                    return new EmotionSummary(
                            emotion,
                            count,
                            percentage);
                })
                .sorted(
                        Comparator.comparingLong(
                                EmotionSummary::getCount).reversed())
                .toList();

        return new EmotionSummarySnapshot(
                total,
                emotions);
    }
}