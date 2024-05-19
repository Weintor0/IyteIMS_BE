package edu.iyte.ceng.internship.ims.service.mapper;

import edu.iyte.ceng.internship.ims.entity.Notification;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.model.request.notification.CreateNotificationRequest;
import edu.iyte.ceng.internship.ims.model.response.notifications.NotificationResponse;
import edu.iyte.ceng.internship.ims.util.DateUtil;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {
    public NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .sourceUserId(notification.getSourceUser().getId())
                .sendDate(notification.getSendDate())
                .read(notification.getRead())
                .content(notification.getContent())
                .build();
    }

    public Notification fromRequest(CreateNotificationRequest request, User sourceUser, User destinationUser) {
        return Notification.builder()
                .sourceUser(sourceUser)
                .destinationUser(destinationUser)
                .sendDate(DateUtil.asDate(DateUtil.now()))
                .read(false)
                .content(request.getContent())
                .build();
    }
}
