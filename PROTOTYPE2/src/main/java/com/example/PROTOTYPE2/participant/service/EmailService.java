package com.example.PROTOTYPE2.participant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a survey link email to a participant.
     *
     * @param toEmail         participant's email address
     * @param participantName participant's name
     * @param token           the UUID survey token
     * @param surveyName      name of the survey being sent
     */
    public void sendSurveyLink(String toEmail, String participantName, String token, String surveyName) {
        String surveyUrl = frontendUrl + "/survey/" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("You have a new survey: " + surveyName);
            helper.setText(buildEmailBody(participantName, surveyName, surveyUrl), true);

            mailSender.send(message);
            log.info("Survey email sent to {} for survey '{}'", toEmail, surveyName);

        } catch (MessagingException e) {
            // Log the error but don't crash the application — token is still saved
            log.error("Failed to send survey email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildEmailBody(String name, String surveyName, String surveyUrl) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #1a0a0a; color: #fff; padding: 32px; border-radius: 16px;">
                    <div style="margin-bottom: 24px;">
                        <span style="background: #e11d48; color: white; padding: 6px 12px; border-radius: 8px; font-weight: bold; font-size: 14px;">Prototype</span>
                    </div>
                    <h2 style="color: #fff; margin-bottom: 8px;">Hi %s,</h2>
                    <p style="color: #fda4af; margin-bottom: 24px;">
                        You have a new survey ready to complete: <strong style="color: #fff;">%s</strong>
                    </p>
                    <a href="%s"
                       style="display: inline-block; background: #e11d48; color: white; padding: 14px 28px; border-radius: 12px; text-decoration: none; font-weight: bold; font-size: 15px;">
                        Complete Survey →
                    </a>
                    <p style="color: #9f1239; font-size: 12px; margin-top: 24px;">
                        This link is unique to you. Do not share it with others.
                    </p>
                </div>
                """.formatted(name, surveyName, surveyUrl);
    }
}
