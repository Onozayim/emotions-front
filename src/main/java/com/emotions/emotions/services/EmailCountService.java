package com.emotions.emotions.services;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.emotions.emotions.entities.EmailCount;
import com.emotions.emotions.entities.EmailCountDtoSum;

public interface EmailCountService {
    public EmailCount getLastCount();
    public EmailCountDtoSum getSum(Specification<EmailCount> spec);
    public List<EmailCount> getEmailCounts(Specification<EmailCount> spec);

    public void incrementPrimaryOrCompound(EmailCount count, String emotion);
    public void incrementSecondary(EmailCount count, String emotion);
    public void decrementPrimaryOrCompound(EmailCount count, String emotion);
    public void decrementSecondary(EmailCount count, String emotion);
}