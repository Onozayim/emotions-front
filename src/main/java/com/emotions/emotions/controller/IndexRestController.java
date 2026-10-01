package com.emotions.emotions.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class IndexRestController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailCountRepository emailCountRepository;

    @Autowired
    private EmailCountService emailCountService;

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

    @PutMapping(value = "email/{id}")
    public String changeEmailEmotion(@PathVariable("id") Long id, @RequestBody UpdateEmailDto body) throws Exception {
        Email email = emailRepository.findById(id).orElseThrow(() -> new Exception("Email not found"));
        
        String oldPrimary = email.getEmotion();
        String oldCompound = email.getCompoundEmotion();
        String oldSecondary = email.getSecondaryEmotion();
        
        LocalDate emailDate = email.getCreatedAt()
        .toLocalDate();
        
        EmailCount count = emailCountRepository
        .findByCreatedAt(emailDate)
        .orElseThrow(() -> new Exception("Count record not found for " + emailDate));
        
        emailCountService.decrementPrimaryOrCompound(count, oldPrimary);
        emailCountService.decrementPrimaryOrCompound(count, oldCompound);
        emailCountService.decrementSecondary(count, oldSecondary);
        
        email.setFixedEmotion(body.getPrimaryEmotion());
        email.setFixedSecondaryEmotion(body.getSecondaryEmotion());
        email.setFixedCompoundEmotion(body.getCompoundEmotion());
        
        emailCountService.incrementPrimaryOrCompound(count, body.getPrimaryEmotion());
        emailCountService.incrementPrimaryOrCompound(count, body.getCompoundEmotion());
        emailCountService.incrementSecondary(count, body.getSecondaryEmotion());

        emailCountRepository.save(count);
        emailRepository.save(email);
        return "success";
    }
}
