package com.example.forumapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBlockNotification(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Cont blocat - Forum App");
        message.setText("Salut, " + username + ",\n\nContul tău a fost blocat din cauza unei încalcari a regulilor.\nTe rugăm să contactezi un administrator pentru mai multe detalii . ");

        mailSender.send(message);
    }

    public void sendUnblockNotification(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Cont deblocat - Forum App");
        message.setText("Salut, " + username + ",\n\nContul tău a fost deblocat. Poți accesa din nou aplicația Forum App.");

        mailSender.send(message);
    }
}
