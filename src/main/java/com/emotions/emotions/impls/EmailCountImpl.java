package com.emotions.emotions.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emotions.emotions.entities.EmailCount;
import com.emotions.emotions.repositories.EmailCountRepository;
import com.emotions.emotions.services.EmailCountService;

@Service
public class EmailCountImpl implements EmailCountService {
    @Autowired
    EmailCountRepository emailCountRepository;


    public EmailCount getLastCount() {
        return emailCountRepository.getLastRecord();
    }
}
