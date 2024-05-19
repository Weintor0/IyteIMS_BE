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

    /** Get a particular internship record */
    @GetMapping(path = "/get/{internshipId}")
    public ResponseEntity<Internship> getInternshipById(String id) {
        return new ResponseEntity<>(internshipService.getInternshipById(id), HttpStatus.OK);
    }

    /** Get all internship records */
    @GetMapping(path = "/get-all")
    public ResponseEntity<List<Internship>> getInternship() {
        return new ResponseEntity<>(internshipService.getInternships(), HttpStatus.OK);
    }

    /** Send Application Letter */
    @PostMapping(path = "/application-letter/send/{offerId}",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SendApplicationLetterResponse> sendApplicationLetter(
            @PathVariable("offerId") String offerId,
            @RequestPart("file") MultipartFile file) throws IOException {
        SendApplicationLetterResponse response = internshipService.sendApplicationLetter(offerId, file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** Evaluate Application Letter */
    @PatchMapping(path = "/application-letter/evaluate/{internshipId}")
    public ResponseEntity<HttpStatus> updateApplicationLetterAcceptance(
            @PathVariable("internshipId") String internshipId,
            UpdateDocumentAcceptanceRequest request) {
        internshipService.updateApplicationLetterAcceptance(internshipId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Send Application Form */
    @PostMapping(path = "/application-form/send/{internshipId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> sendApplicationForm(
            @PathVariable("internshipId") String internshipId,
            @RequestPart("file") MultipartFile file) throws IOException {
        internshipService.sendApplicationForm(internshipId, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Evaluate Application Form */
    @PatchMapping(path = "/application-form/evaluate/{internshipId}")
    public ResponseEntity<HttpStatus> updateApplicationFormAcceptance(
            @PathVariable("internshipId") String internshipId,
            UpdateDocumentAcceptanceRequest request) {
        internshipService.updateApplicationFormAcceptance(internshipId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
