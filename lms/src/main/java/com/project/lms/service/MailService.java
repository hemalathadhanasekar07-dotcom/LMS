package com.project.lms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendUserRegistrationMail(String toEmail, String name) {
        Context context = new Context();
        context.setVariable("name", name);

        String html = templateEngine.process("user-registration", context);
        sendHtmlMail(toEmail, "Welcome to LMS", html);
    }

    public void sendCoursePublishedMail(String toEmail, String courseTitle) {
        Context context = new Context();
        context.setVariable("courseTitle", courseTitle);

        String html = templateEngine.process("course-published", context);
        sendHtmlMail(toEmail, "New Course Published", html);
    }

    private void sendHtmlMail(String toEmail, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("MAIL_SEND_FAILED");
        }
    }
}