package com.emotions.emotions.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.emotions.emotions.entities.SurveyTemplate;
import java.util.Optional;

public interface SurveyTemplateRepository extends JpaRepository<SurveyTemplate, Integer> {
    Optional<SurveyTemplate> findFirstByEmotion(String emotion);
}