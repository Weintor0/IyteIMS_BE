package edu.iyte.ceng.internship.ims.service;


import edu.iyte.ceng.internship.ims.entity.Announcement;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.AnnouncementRequest;
import edu.iyte.ceng.internship.ims.model.response.AnnouncementResponse;
import edu.iyte.ceng.internship.ims.model.response.InternshipOfferResponse;
import edu.iyte.ceng.internship.ims.repository.AnnouncementRepository;
import edu.iyte.ceng.internship.ims.service.mapper.AnnouncementMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class AnnouncementService {
    private final AuthenticationService authenticationService;
    private final AnnouncementMapper announcementMapper;
    private final AnnouncementRepository announcementRepository;

    public AnnouncementResponse createAnnouncement(AnnouncementRequest announcementRequest) {
        ensureCreateandUpdatePrivilege();
        Announcement announcement = new Announcement();
        announcementMapper.fromRequest(announcement, announcementRequest);
        announcementRepository.save(announcement);
        return announcementMapper.fromEntity(announcement);

    }

    public Page<AnnouncementResponse> listAnnouncements(Pageable pageable) {
            return announcementRepository.findAll(pageable).map(announcementMapper::fromEntity);

    }

    @Transactional(rollbackFor = Throwable.class)
    public AnnouncementResponse updateAnnouncement(AnnouncementRequest announcementRequest , String id) {
        ensureCreateandUpdatePrivilege();
        Announcement announcement = announcementRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.ResourceMissing,
                "Announcement with" + id  + " does not exist"));
        announcementMapper.fromRequest(announcement , announcementRequest);
        announcementRepository.save(announcement);
        return announcementMapper.fromEntity(announcement);
    }

    private void ensureCreateandUpdatePrivilege() {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser.getUserRole() != UserRole.InternshipCoordinator) {
            throw new BusinessException(ErrorCode.Unauthorized, "A  internship coordinator can only  create an internship offer");
        }

    }
}
