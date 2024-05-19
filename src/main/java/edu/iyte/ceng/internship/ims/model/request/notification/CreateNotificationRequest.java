package edu.iyte.ceng.internship.ims.model.request.notification;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateNotificationRequest {
    private String content;
}
