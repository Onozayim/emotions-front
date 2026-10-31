package com.emotions.emotions.services;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.emotions.emotions.entities.SurveyAnswer;
import com.emotions.emotions.entities.SurveyTemplate;
import com.emotions.emotions.repositories.EmailRepository;
import com.emotions.emotions.repositories.SurveyAnswerRepository;
import com.emotions.emotions.repositories.SurveyTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SurveyEmotionService {

    private final EmailRepository emailRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyTemplateRepository surveyTemplateRepository;

    private static final Set<String> POSITIVE = Set.of(
        "joy", "love", "surprise"
    );

    private static final Set<String> NEUTRAL = Set.of(
        "intrigue", "nostalgia", "justice"
    );

    private static final Set<String> NEGATIVE = Set.of(
        "sadness", "anger", "fear", "disgust",
        "contempt", "anxiety", "betrayal",
        "repulsion", "aversion", "hate"
    );

    public void updatePendingSurvey(String emailAddress) {

        List<Object[]> results =
                emailRepository.getEmotionDistribution(emailAddress);

        Map<String, Long> categories = new LinkedHashMap<>();
        categories.put("positive", 0L);
        categories.put("neutral", 0L);
        categories.put("negative", 0L);

        for (Object[] row : results) {
            String emotion = ((String) row[0]).toLowerCase();
            long count = ((Number) row[1]).longValue();

            String category = getEmotionCategory(emotion);

            categories.merge(category, count, Long::sum);
        }

        long total = categories.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        if (total == 0) {
            return;
        }

        long highest = categories.values()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        // Preserve a consistent policy for tied categories.
        // Here, ties resolve to neutral.
        long ties = categories.values()
                .stream()
                .filter(value -> value == highest)
                .count();

        String dominantCategory = ties > 1
                ? "neutral"
                : categories.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() == highest)
                    .findFirst()
                    .orElseThrow()
                    .getKey();

        Optional<SurveyAnswer> unansweredSurvey =
        surveyAnswerRepository.findFirstByEmailAndRespondedAtIsNull(
                emailAddress
        );

        if (unansweredSurvey.isEmpty()) {
            return;
        }

        SurveyTemplate template = surveyTemplateRepository
                .findFirstByEmotion(dominantCategory)
                .orElseThrow(() ->
                    new IllegalStateException(
                        "Survey template not found: "
                        + dominantCategory
                    )
                );

        SurveyAnswer survey = unansweredSurvey.get();

        survey.setSurvey(template);
        survey.setLastActivityAt(LocalDateTime.now());

        surveyAnswerRepository.save(survey);
    }

    private String getEmotionCategory(String emotion) {

        if (POSITIVE.contains(emotion)) {
            return "positive";
        }

        if (NEGATIVE.contains(emotion)) {
            return "negative";
        }

        if (NEUTRAL.contains(emotion)) {
            return "neutral";
        }

        throw new IllegalArgumentException(
            "Unknown emotion: " + emotion
        );
    }
}