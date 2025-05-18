package kr.ac.korea.gdg.disasterassistantforblind.modules.notification.service;

import kr.ac.korea.gdg.disasterassistantforblind.modules.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a notification to a specific user
     *
     * @param user The user to send the notification to
     * @param message The message to send
     */
    public void sendNotification(User user, String message) {
        try {
            String destination = "/topic/user/" + user.getId();
            
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "DISASTER_GUIDANCE");
            notification.put("message", message);
            notification.put("timestamp", LocalDateTime.now().toString());
            
            log.info("Sending notification to user {}: {}", user.getId(), message);
            messagingTemplate.convertAndSend(destination, notification);
        } catch (Exception e) {
            log.error("Error sending notification to user: {}", user.getId(), e);
        }
    }
}