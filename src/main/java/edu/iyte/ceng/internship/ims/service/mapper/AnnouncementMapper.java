package edu.iyte.ceng.internship.ims.service.mapper;

import edu.iyte.ceng.internship.ims.entity.Announcement;
import edu.iyte.ceng.internship.ims.model.request.AnnouncementRequest;
import edu.iyte.ceng.internship.ims.model.response.AnnouncementResponse;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementMapper {

    public AnnouncementResponse fromEntity(Announcement announcement) {
        AnnouncementResponse response = AnnouncementResponse.builder().
                announcementId(announcement.getId()).build();

        return response;
    }
    public Announcement fromRequest(Announcement announcement , AnnouncementRequest request) {

        announcement.setTitle(request.getTitle());
        announcement.setContext(request.getContext());
        announcement.setAttachmentUrl(request.getAttachmentUrl());
        return announcement;
    }

}
