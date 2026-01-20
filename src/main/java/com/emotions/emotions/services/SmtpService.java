package com.emotions.emotions.services;

import org.thymeleaf.context.Context;

import com.emotions.emotions.entities.EmailDetails;

public interface SmtpService {
    public String sendSimplMail(EmailDetails details);
    public String sendHtmlEmail(EmailDetails emailDetails, Context context, String template);
}
