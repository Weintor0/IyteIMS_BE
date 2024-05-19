package edu.iyte.ceng.internship.ims.model.response.notifications;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class NotificationResponse {
    private String id;
    private String sourceUserId;
    private Date sendDate;
    private Boolean read;
    private String content;
}
