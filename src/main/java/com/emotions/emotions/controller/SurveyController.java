package com.emotions.emotions.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.emotions.emotions.entities.Paginator;
import com.emotions.emotions.entities.SurveyAnswer;
import com.emotions.emotions.entities.SurveyDefinition;
import com.emotions.emotions.entities.SurveyQuestion;
import com.emotions.emotions.entities.SurveyQuestionAnswerDto;
import com.emotions.emotions.entities.SurveySnapshot;
import com.emotions.emotions.entities.SurveyStatus;
import com.emotions.emotions.entities.SurveyTemplate;
import com.emotions.emotions.helpers.PaginatorController;
import com.emotions.emotions.repositories.SurveyAnswerRepository;
import com.emotions.emotions.services.SurveyAnswerService;
import com.emotions.emotions.services.SurveyTemplateService;
import com.emotions.emotions.specifications.SurveyAnswerSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SurveyController {
        private final SurveyAnswerService surveyAnswerService;
        private final ObjectMapper objectMapper;
        private final SurveyTemplateService surveyTemplateService;
        private final SurveyAnswerRepository surveyAnswerRepository;
        private final PaginatorController paginatorController;

        public SurveyController(
                        SurveyAnswerService surveyAnswerService,
                        ObjectMapper objectMapper,
                        SurveyTemplateService surveyTemplateService,
                        SurveyAnswerRepository surveyAnswerRepository,
                        PaginatorController paginatorController) {
                this.surveyAnswerService = surveyAnswerService;
                this.objectMapper = objectMapper;
                this.surveyTemplateService = surveyTemplateService;
                this.surveyAnswerRepository = surveyAnswerRepository;
                this.paginatorController = paginatorController;
        }

        @GetMapping("/survey/{token}")
        public String asnwerSurvey(@PathVariable String token, Model model) throws JsonProcessingException {
                SurveyAnswer answer = surveyAnswerService.findByToken(token)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Survey not found"));

                if (answer.getStatus().equals(SurveyStatus.ANSWERED))
                        return "redirect:/survey/" + token + "/complete";

                if (answer.getStatus().equals(SurveyStatus.EXPIRED))
                        return "redirect:/survey/" + token + "/expired";

                SurveyDefinition definition = objectMapper.readValue(answer.getSurvey().getTemplate(),
                                SurveyDefinition.class);

                model.addAttribute("surveyAnswer", answer);
                model.addAttribute("survey", answer.getSurvey());
                model.addAttribute("questions", definition.getQuestions());

                return "surveys/answer";
        }

        @GetMapping("/surveys/{id}")
        public String getSurveyInfo(@PathVariable Integer id, Model model) {
                try {
                        SurveyAnswer surveyAnswer = surveyAnswerService.findById(id)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Survey not found"));

                        SurveyTemplate survey = surveyAnswer.getSurvey();
                        List<SurveyQuestionAnswerDto> questionAnswers = new ArrayList<>();

                        String subject;
                        String message;
                        String emotion;
                        List<SurveyQuestion> questions;

                        if (surveyAnswer.getTemplateSnapshot() != null
                                        && !surveyAnswer.getTemplateSnapshot().isBlank()) {
                                SurveySnapshot snapshot = objectMapper.readValue(surveyAnswer.getTemplateSnapshot(),
                                                SurveySnapshot.class);

                                subject = snapshot.getSubject();
                                message = snapshot.getMessage();
                                emotion = snapshot.getEmotion();
                                questions = snapshot.getQuestions();
                        } else {
                                SurveyDefinition definition = objectMapper.readValue(survey.getTemplate(),
                                                SurveyDefinition.class);

                                subject = survey.getSubject();
                                message = survey.getMessage();
                                emotion = survey.getEmotion();
                                questions = definition.getQuestions();
                        }

                        Map<String, Object> answers = new HashMap<>();

                        if (surveyAnswer.getAnswers() != null && !surveyAnswer.getAnswers().isBlank()) {
                                answers = objectMapper.readValue(surveyAnswer.getAnswers(),
                                                new TypeReference<Map<String, Object>>() {
                                                });
                        }

                        for (SurveyQuestion question : questions) {
                                Object answer = answers.get(question.getId());
                                questionAnswers.add(new SurveyQuestionAnswerDto(
                                                question.getId(),
                                                question.getType(),
                                                question.getLabel(),
                                                answer));
                        }

                        model.addAttribute("surveyAnswer", surveyAnswer);
                        model.addAttribute("subject", subject);
                        model.addAttribute("message", message);
                        model.addAttribute("emotion", emotion);
                        model.addAttribute("questionAnswers", questionAnswers);

                        return "surveys/detail";
                } catch (JsonProcessingException e) {
                        throw new IllegalStateException("Invalid survey template", e);
                }
        }

        @GetMapping("/surveys")
        public String surveys(
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
                        @RequestParam(required = false) String emotion,
                        @RequestParam(required = false) SurveyStatus status,
                        @RequestParam(required = false) String email,
                        @PageableDefault(size = 5, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {

                Specification<SurveyAnswer> spec = Specification.<SurveyAnswer>unrestricted()
                                .and(SurveyAnswerSpecification.hasEmotion(emotion))
                                .and(SurveyAnswerSpecification.hasStatus(status))
                                .and(SurveyAnswerSpecification.hasEmail(email))
                                .and(SurveyAnswerSpecification.fromDate(from))
                                .and(SurveyAnswerSpecification.toDate(to));

                Page<SurveyAnswer> page = surveyAnswerRepository.findAll(spec, pageable);

                Paginator paginator = paginatorController.generatePaginator(page);

                model.addAttribute("surveys", page.getContent());
                model.addAttribute("pageNumbers", paginator.getPageNumbers());
                model.addAttribute("prevDots", paginator.isPrevDots());
                model.addAttribute("nextDots", paginator.isNextDots());

                model.addAttribute("page", page);

                model.addAttribute("from", from);
                model.addAttribute("to", to);
                model.addAttribute("emotion", emotion);
                model.addAttribute("status", status);
                model.addAttribute("email", email);

                model.addAttribute("statuses", SurveyStatus.values());

                return "surveys/surveys";
        }

        @PostMapping("/survey-templates/{id}")
        public String updateTemplate(
                        @PathVariable Integer id,
                        @RequestParam String subject,
                        @RequestParam String message,
                        @RequestParam String emotion,
                        @RequestParam String template) {

                SurveyTemplate surveyTemplate = surveyTemplateService
                                .findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Survey template not found"));

                surveyTemplate.setSubject(subject);
                surveyTemplate.setMessage(message);
                surveyTemplate.setEmotion(emotion);
                surveyTemplate.setTemplate(template);

                surveyTemplateService.save(surveyTemplate);

                return "redirect:/survey-templates";
        }

        @GetMapping("/survey-templates")
        public String surveyTemplatesList(Model model) {
                model.addAttribute("templates", surveyTemplateService.findAll());
                return "surveys/template-list";
        }

        @GetMapping("/survey-templates/{id}/edit")
        public String getMethodName(@PathVariable Integer id, Model model) {
                SurveyTemplate template = surveyTemplateService.findById(id).orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey template not found"));

                model.addAttribute("surveyTemplate", template);
                return "surveys/template-edit";
        }

        @PostMapping("/survey/{token}")
        public String submitSurvey(@PathVariable String token, @RequestParam Map<String, String> answers) {
                surveyAnswerService.submitAnswer(token, answers);

                return "redirect:/survey/" + token + "/complete";
        }

        @GetMapping("/survey/{token}/complete")
        public String surveyComplete() {
                return "surveys/complete";
        }

        @GetMapping("/survey/{token}/expired")
        public String surveyExpired() {
                return "surveys/expired";
        }
}

