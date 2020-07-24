package com.zerfu.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMail {
    @Value("${spring.mail.username}")
    private String email;
    @Autowired
    private JavaMailSender mailSender;
    /**
     * 简单文本邮件
     * @param toEmail 接收者邮件
     * @param subject 邮件主题
     * @param contnet 邮件内容
     */
    public boolean sendSimpleMail(String toEmail, String subject, String contnet){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(contnet);
            message.setFrom(email);
            mailSender.send(message);
            return true;
        } catch (MailException e) {
            e.printStackTrace();
        }
        return false;
    }
}
