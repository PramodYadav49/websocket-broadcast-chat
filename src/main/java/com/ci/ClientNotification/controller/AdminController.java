package com.ci.ClientNotification.controller;

import com.ci.ClientNotification.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final NotificationService notificationService;

    public AdminController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/broadcast")
    public String braodcastMessage(@RequestBody String message){
        notificationService.sendGlobalNotification("ADMIN ALERT",message);
        return "Notification send to all active users.";
    }

    public static class PrivateMessageRequest {
        public String targetUsername;
        public String title;
        public String content;
    }

    // New Endpoint to send a direct message
    @PostMapping("/send-private")
    public String sendPrivateMessage(@RequestBody PrivateMessageRequest request) {
        notificationService.sendPrivateNotification(
                request.targetUsername,
                request.title,
                request.content
        );
        return "Private message sent to " + request.targetUsername;
    }
}
