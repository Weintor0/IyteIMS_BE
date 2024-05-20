package edu.iyte.ceng.internship.ims.controller;


import edu.iyte.ceng.internship.ims.model.request.AnnouncementRequest;
import edu.iyte.ceng.internship.ims.model.response.AnnouncementResponse;

import edu.iyte.ceng.internship.ims.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    @PostMapping("/createannouncement")
    public AnnouncementResponse createInternship(@Valid @RequestBody AnnouncementRequest announcementRequest) {
        return announcementService.createAnnouncement(announcementRequest);
    }
    @GetMapping("/list")
    public Page<AnnouncementResponse> getAllAnnouncements(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return announcementService.listAnnouncements(PageRequest.of(page, size));
    }
    @PutMapping("/update/{announcementId}")
    public AnnouncementResponse updateInternship(@Valid @RequestBody AnnouncementRequest announcementRequest, @PathVariable("announcementId")String announcementId){
        return announcementService.updateAnnouncement(announcementRequest, announcementId);
    }
}
