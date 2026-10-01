package com.emotions.emotions.impls;

import org.springframework.stereotype.Service;
import com.emotions.emotions.entities.SurveyTemplate;
import com.emotions.emotions.repositories.SurveyTemplateRepository;
import com.emotions.emotions.services.SurveyTemplateService;
import java.util.List;
import java.util.Optional;

@Service
public class SurveyTemplateServiceImpl implements SurveyTemplateService {

    private final SurveyTemplateRepository surveyTemplateRepository;

    public SurveyTemplateServiceImpl(SurveyTemplateRepository surveyTemplateRepository) {
        this.surveyTemplateRepository = surveyTemplateRepository;
    }

    @Override
    public List<SurveyTemplate> findAll() {
        return surveyTemplateRepository.findAll();
    }

    @Override
    public Optional<SurveyTemplate> findById(Integer id) {
        return surveyTemplateRepository.findById(id);
    }

    @Override
    public Optional<SurveyTemplate> findByEmotion(String emotion) {
        return surveyTemplateRepository
                .findFirstByEmotion(emotion);
    }

    @Override
    public SurveyTemplate save(SurveyTemplate surveyTemplate) {
        return surveyTemplateRepository.save(surveyTemplate);
    }

    @Override
    public void deleteById(Integer id) {
        surveyTemplateRepository.deleteById(id);
    }
}