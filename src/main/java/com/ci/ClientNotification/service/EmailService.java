package com.ci.ClientNotification.service;


import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    //JavaMailSernder is the interface it extends MailSender It have default message Send it take SimpleMailMessage

    @Autowired
    private JavaMailSender mailSender;

    @PreDestroy
    public void serviceShutDown(){
        //SimpleMailMessage have Variable Like to,Subject,Text,CC we are carating object of SimpleMailMessage
        SimpleMailMessage message=new SimpleMailMessage();
        //Set Where we have to send the Mail
        message.setTo(new String[]{"pramod@chandikainnov.com", "ratnender@chandikainnov.com"});

        //Set Subject What is the Mail Subject
        message.setSubject("ALERT : Service down");

        //Set Body of the Mail
        message.setText("The Broadcast Service is shutting down");

        //Send Mail through JavaMailSender it extend MailSender it have method send
        mailSender.send(message);
    }

    public void sendMail(String[]to, String subject, String body) {
        //SimpleMailMessage have Variable Like to,Subject,Text,CC we are carating object of SimpleMailMessage
        SimpleMailMessage message = new SimpleMailMessage();

        //Set Where we have to send the Mail
        message.setTo(to);
        //Set Subject What is the Mail Subject
        message.setSubject(subject);
        //Set Body of the Mail
        message.setText(body);
        //Send Mail through JavaMailSender it extend MailSender it have method send
        mailSender.send(message);
    }
}

