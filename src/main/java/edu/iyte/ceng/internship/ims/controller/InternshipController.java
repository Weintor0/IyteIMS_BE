package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.entity.Internship;
import edu.iyte.ceng.internship.ims.model.request.internship.UpdateDocumentAcceptanceRequest;
import edu.iyte.ceng.internship.ims.model.response.internship.SendApplicationLetterResponse;
import edu.iyte.ceng.internship.ims.service.InternshipService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @GetMapping(path = "/get/{internshipId}")
    public ResponseEntity<Internship> getInternshipById(String id) {
        return new ResponseEntity<>(internshipService.getInternshipById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/get-all")
    public ResponseEntity<List<Internship>> getInternship() {
        return new ResponseEntity<>(internshipService.getInternships(), HttpStatus.OK);
    }

    @PostMapping(path = "/application-letter/send/{offerId}",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SendApplicationLetterResponse> sendApplicationLetter(
            @PathVariable("offerId") String offerId,
            @RequestPart("file") MultipartFile file) throws IOException {
        SendApplicationLetterResponse response = internshipService.sendApplicationLetter(offerId, file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(path = "/application-letter/evaluate/{internshipId}")
    public ResponseEntity<HttpStatus> updateApplicationLetterAcceptance(
            @PathVariable("internshipId") String internshipId,
            UpdateDocumentAcceptanceRequest request) {
        internshipService.updateApplicationLetterAcceptance(internshipId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
