package com.emotions.emotions.impls;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.emotions.emotions.entities.EmailDetails;
import com.emotions.emotions.entities.EmotionSummary;
import com.emotions.emotions.entities.EmotionSummarySnapshot;
import com.emotions.emotions.entities.SurveyAnswer;
import com.emotions.emotions.entities.SurveyTemplate;
import com.emotions.emotions.services.EmotionChartService;
import com.emotions.emotions.services.SmtpService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SmtpServiceImpl implements SmtpService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmotionChartService emotionChartService;
    private final ObjectMapper objectMapper;

    public SmtpServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine,
            EmotionChartService emotionChartService, ObjectMapper objectMapper) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.emotionChartService = emotionChartService;
        this.objectMapper = objectMapper;
    }

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public String sendSimplMail(EmailDetails details) {
        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            System.out.println("MAIL SENT");
            return "Mail Sent Successfully...";
        }

        catch (Exception e) {
            System.out.println("EXCEPTIOn");
            System.out.println(e.getMessage());
            return "Error while Sending Mail";
        }
    }

    @Override
    public String sendHtmlEmail(EmailDetails details, Context context, String template) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setSubject(details.getSubject());

            String process = templateEngine.process(template, context);
            System.out.println(process);

            mimeMessageHelper.setText(process, true);

            javaMailSender.send(mimeMessage);
            return "OK";
        }

        // Catch block to handle MessagingException
        catch (MessagingException e) {
            // Display message when exception occurred
            return "Error while sending mail!!!";
        }
    }

    private void sendHtmlEmailOrThrow(
            EmailDetails details,
            Context context,
            String template) {

        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "UTF-8");

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());

            String html = templateEngine.process(
                    template,
                    context);

            helper.setText(html, true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {

            throw new IllegalStateException(
                    "Could not send email to "
                            + details.getRecipient(),
                    e);
        }
    }

    @Override
    public void sendSurvey(SurveyAnswer surveyAnswer) {

        SurveyTemplate survey = surveyAnswer.getSurvey();

        String surveyUrl = baseUrl
                + "/survey/"
                + surveyAnswer.getToken();

        EmailDetails details = new EmailDetails();

        details.setRecipient(
                surveyAnswer.getEmail());

        details.setSubject(
                survey.getSubject());

        Context context = new Context();

        context.setVariable(
                "message",
                survey.getMessage());

        context.setVariable(
                "surveyUrl",
                surveyUrl);

        sendHtmlEmailOrThrow(details, context, "survey");
    }

    @Override
    public void sendEmotionSummary(
            SurveyAnswer surveyAnswer,
            List<EmotionSummary> summary,
            byte[] chart) {

        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "UTF-8");

            helper.setFrom(sender);
            helper.setTo(
                    surveyAnswer.getEmail());

            helper.setSubject(
                    "Your email emotion summary");

            Context context = new Context();

            context.setVariable(
                    "summary",
                    summary);

            context.setVariable(
                    "email",
                    surveyAnswer.getEmail());

            String html = templateEngine.process(
                    "emails/emotion-summary",
                    context);

            helper.setText(html, true);

            helper.addInline(
                    "emotionChart",
                    new ByteArrayResource(chart),
                    "image/png");

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {

            throw new IllegalStateException(
                    "Could not send emotion summary",
                    e);
        }
    }

    @Override
    public void sendEmotionSummary(SurveyAnswer surveyAnswer) {
        try {
            EmotionSummarySnapshot snapshot = objectMapper.readValue(
                    surveyAnswer.getEmotionSummarySnapshot(),
                    EmotionSummarySnapshot.class);

            byte[] chartImage = emotionChartService.generateChart(snapshot);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "UTF-8");

            helper.setFrom(sender);
            helper.setTo(surveyAnswer.getEmail());

            helper.setSubject("Your Email Emotion Summary");

            Context context = new Context();
            context.setVariable("summary", snapshot.getEmotions());

            context.setVariable("totalEmails", snapshot.getTotalEmails());

            String html = templateEngine.process("surveys/emotion-summary", context);

            helper.setText(html, true);

            helper.addInline(
                    "emotionChart",
                    new ByteArrayResource(
                            chartImage),
                    "image/png");

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not send emotion summary to "
                            + surveyAnswer.getEmail(),
                    e);
        }
    }

}
