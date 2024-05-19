package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.entity.Notification;
import edu.iyte.ceng.internship.ims.model.request.notification.CreateNotificationRequest;
import edu.iyte.ceng.internship.ims.model.response.notifications.NotificationResponse;
import edu.iyte.ceng.internship.ims.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/incoming")
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        List<NotificationResponse> result = notificationService.getNotifications();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<HttpStatus> markAsRead(@PathVariable("notificationId") String notificationId) {
        notificationService.markAsRead(notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create/{destinationUserId}")
    public ResponseEntity<NotificationResponse> createNotification(@PathVariable("destinationUserId") String destinationUserId,
                                                   CreateNotificationRequest request) {
        NotificationResponse result = notificationService.createNotification(destinationUserId, request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
