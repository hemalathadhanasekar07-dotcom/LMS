package com.project.lms.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendUserRegistrationMail(String toEmail, String name) {

        try {
            Context context = new Context();
            context.setVariable("name", name);

            String htmlContent = templateEngine.process("user-registration", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Welcome to LMS ");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("User registration email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send registration email to {}", toEmail, e);
        }
    }

    public void sendCoursePublishedMail(String toEmail, String name, String courseTitle) {

        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("courseTitle", courseTitle);

            String htmlContent = templateEngine.process("course-published", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("New Course Published!");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Course publish email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send course publish email to {}", toEmail, e);
        }
    }

    public void sendUserApprovedMail(String toEmail, String name) {

        try {
            Context context = new Context();
            context.setVariable("name", name);

            String htmlContent = templateEngine.process("user-approved", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Account Approved — You Can Now Access LMS");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("User approval email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send approval email to {}", toEmail, e);
        }
    }

    public void sendUserRejectedMail(String toEmail, String name) {

        try {
            Context context = new Context();
            context.setVariable("name", name);

            String htmlContent = templateEngine.process("user-rejected", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Account Registration Update");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("User rejection email sent to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send rejection email to {}", toEmail, e);
        }
    }
}