package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.Notification;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.notification.CreateNotificationRequest;
import edu.iyte.ceng.internship.ims.model.response.notifications.NotificationResponse;
import edu.iyte.ceng.internship.ims.repository.NotificationRepository;
import edu.iyte.ceng.internship.ims.service.mapper.NotificationMapper;
import edu.iyte.ceng.internship.ims.util.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final NotificationMapper notificationMapper;

    public List<NotificationResponse> getNotifications() {
        User currentUser = userService.getUserById(SecurityContextHolder.getContext().getAuthentication().getName()); // TODO
        List<NotificationResponse> notificationList = new ArrayList<>();
        for (Notification notification : notificationRepository.findNotificationByDestinationUser(currentUser)) {
            notificationList.add(notificationMapper.fromEntity(notification));
        }
        return notificationList;
    }

    public void markAsRead(String notificationId) {
        User currentUser = userService.getUserById(SecurityContextHolder.getContext().getAuthentication().getName()); // TODO

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Notification not found.")
        );

        if (!notification.getDestinationUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.Unauthorized,
                    "A user cannot mark another users' notification as read.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public NotificationResponse createNotification(String destinationUserId, CreateNotificationRequest request) {
        User currentUser = userService.getUserById(SecurityContextHolder.getContext().getAuthentication().getName()); // TODO
        if (currentUser.getUserRole() != UserRole.InternshipCoordinator) {
            throw new BusinessException(ErrorCode.Unauthorized,
                    "Only the internship coordinator is allowed to send arbitrary notifications.");
        }

        User destinationUser = userService.getUserById(destinationUserId);
        Notification notification = notificationMapper.fromRequest(request, currentUser, destinationUser);
        notificationRepository.save(notification);
        return notificationMapper.fromEntity(notification);
    }
}
