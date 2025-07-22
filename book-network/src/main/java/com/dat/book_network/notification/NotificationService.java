package com.dat.book_network.notification;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Integer userID,Notification notification) {
        log.info("Sending notification to {} with payload {} ", userID, notification);
        try {
            messagingTemplate.convertAndSendToUser(
                    userID.toString(),
                    "/queue/notification",
                    notification
            );
            log.info("Notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending notification: ", e);
        }
    }
}
