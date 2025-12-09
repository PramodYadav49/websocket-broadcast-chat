package com.ci.ClientNotification.service;

import com.ci.ClientNotification.model.NotificationMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    public void sendGlobalNotification(String title,String content){
        NotificationMessage notification=new NotificationMessage();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSender("SYSTEM");
        simpMessagingTemplate.convertAndSend("/topic/global-notifications",notification);
        System.out.println("Broadcasting global notification: "+title);
    }

    public void sendPrivateNotification(String targetUsername, String title, String content) {
        NotificationMessage notification = new NotificationMessage();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSender("PRIVATE_SYSTEM");

        // ✨ NEW METHOD: Targets a specific user's session(s)
        // Spring automatically resolves the final destination: /user/targetUsername/queue/private-messages
        simpMessagingTemplate.convertAndSendToUser(
                targetUsername,                          // The user's ID (username)
                "/queue/private-messages",             // The private destination path for this user
                notification
        );

        System.out.println("Sending private notification to: " + targetUsername + " - " + title);
    }
}
