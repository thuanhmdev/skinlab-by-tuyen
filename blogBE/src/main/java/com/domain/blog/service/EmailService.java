package com.domain.blog.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;


@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    public EmailService(
            JavaMailSender javaMailSender,
            SpringTemplateEngine springTemplateEngine
    ) {

        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml)   {
        MimeMessage mineMessage = this.javaMailSender.createMimeMessage();
           try {
               MimeMessageHelper helper = new MimeMessageHelper(mineMessage, isMultipart, StandardCharsets.UTF_8.name());
               helper.setTo(to);
               helper.setSubject(subject);
               helper.setText(content, isHtml);

               this.javaMailSender.send(mineMessage);
           } catch (MailException | MessagingException e) {
               System.out.println("ERROR SEND EMAIL: " + e);
           }
    }
    @Async
    public void sendEmailFromTemplateSync(String to, String subject, String templateName, Object contextObject)   {
        Context context = new Context();
        context.setVariable("context", contextObject);
        String content = this.springTemplateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }
}
