package com.emotions.emotions.controller;

import com.emotions.emotions.services.SurveyEmotionService;
import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.emotions.emotions.entities.Email;
import com.emotions.emotions.entities.EmailCount;
import com.emotions.emotions.entities.RegisterDto;
import com.emotions.emotions.entities.UpdateEmailDto;
import com.emotions.emotions.entities.User;
import com.emotions.emotions.repositories.EmailCountRepository;
import com.emotions.emotions.repositories.EmailRepository;
import com.emotions.emotions.repositories.UserRepository;
import com.emotions.emotions.services.EmailCountService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class IndexRestController {
    private final SurveyEmotionService surveyEmotionService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final EmailCountRepository emailCountRepository;
    private final EmailCountService emailCountService;

    public IndexRestController(PasswordEncoder passwordEncoder, UserRepository userRepository,
            EmailRepository emailRepository, EmailCountRepository emailCountRepository,
            EmailCountService emailCountService, SurveyEmotionService surveyEmotionService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.emailCountRepository = emailCountRepository;
        this.emailCountService = emailCountService;
        this.surveyEmotionService = surveyEmotionService;
    }

    @PostMapping(value = "/create-user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User saveUser(@Valid @RequestBody RegisterDto registerDto) {
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        return userRepository.save(user);
    }

    @DeleteMapping(value = "users/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);

        return "success";
    }

    @PutMapping("email/{id}")
    @Transactional 
    public String changeEmailEmotion(
            @PathVariable("id") Long id,
            @RequestBody UpdateEmailDto body) {

        Email email = emailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        // Previous effective emotions (including earlier corrections)
        String oldPrimary = email.getFixedEmotion() != null
                ? email.getFixedEmotion()
                : email.getEmotion();

        String oldSecondary = email.getFixedSecondaryEmotion() != null
                ? email.getFixedSecondaryEmotion()
                : email.getSecondaryEmotion();

        String oldCompound = email.getFixedCompoundEmotion() != null
                ? email.getFixedCompoundEmotion()
                : email.getCompoundEmotion();

        LocalDate emailDate = email.getCreatedAt().toLocalDate();

        EmailCount count = emailCountRepository
                .findByCreatedAt(emailDate)
                .orElseThrow(() -> new IllegalStateException(
                        "Count record not found for " + emailDate));

        // Remove previously counted emotions
        emailCountService.decrementPrimaryOrCompound(
                count, oldPrimary);
        emailCountService.decrementPrimaryOrCompound(
                count, oldCompound);
        emailCountService.decrementSecondary(
                count, oldSecondary);

        // Save the corrected emotions
        email.setFixedEmotion(body.getPrimaryEmotion());
        email.setFixedSecondaryEmotion(body.getSecondaryEmotion());
        email.setFixedCompoundEmotion(body.getCompoundEmotion());

        // New effective emotions
        String newPrimary = email.getFixedEmotion() != null
                ? email.getFixedEmotion()
                : email.getEmotion();

        String newSecondary = email.getFixedSecondaryEmotion() != null
                ? email.getFixedSecondaryEmotion()
                : email.getSecondaryEmotion();

        String newCompound = email.getFixedCompoundEmotion() != null
                ? email.getFixedCompoundEmotion()
                : email.getCompoundEmotion();

        // Count the new effective emotions
        emailCountService.incrementPrimaryOrCompound(
                count, newPrimary);
        emailCountService.incrementPrimaryOrCompound(
                count, newCompound);
        emailCountService.incrementSecondary(
                count, newSecondary);

        emailCountRepository.save(count);

        // Flush changes before recalculating the survey
        emailRepository.saveAndFlush(email);

        surveyEmotionService.updatePendingSurvey(email.getFrom());

        return "success";
    }
}
