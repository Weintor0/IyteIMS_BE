package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.entity.Document;
import edu.iyte.ceng.internship.ims.entity.Internship;
import edu.iyte.ceng.internship.ims.model.request.internship.UpdateDocumentAcceptanceRequest;
import edu.iyte.ceng.internship.ims.model.response.internship.InternshipResponse;
import edu.iyte.ceng.internship.ims.model.response.internship.SendApplicationLetterResponse;
import edu.iyte.ceng.internship.ims.service.InternshipService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;
    private final DownloadHelper downloadHelper;

    /** Get a particular internship record */
    @GetMapping(path = "/get/{internshipId}")
    public ResponseEntity<InternshipResponse> getInternshipById(String id) {
        return new ResponseEntity<>(internshipService.getInternshipById(id), HttpStatus.OK);
    }

    /** Get all internship records */
    @GetMapping(path = "/get-all")
    public ResponseEntity<List<InternshipResponse>> getInternship() {
        return new ResponseEntity<>(internshipService.getInternships(), HttpStatus.OK);
    }

    /** Send Application Letter */
    @PostMapping(path = "/application-letter/send/{offerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SendApplicationLetterResponse> sendApplicationLetter(
            @PathVariable("offerId") String offerId,
            @RequestPart("file") MultipartFile file) {
        SendApplicationLetterResponse response = internshipService.sendApplicationLetter(offerId, file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** Get Application Letter */
    @GetMapping(path = "/application-letter/download/{internshipId}")
    public HttpEntity<byte[]> downloadApplicationLetter(@PathVariable("internshipId") String internshipId) {
        Document document = internshipService.getApplicationLetter(internshipId);
        return downloadHelper.fromDocument(document);
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
    @PostMapping(path = "/application-form/send/{internshipId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> sendApplicationForm(
            @PathVariable("internshipId") String internshipId,
            @RequestPart("file") MultipartFile file) {
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

    /** Get Application Form By Student */
    @GetMapping(path = "/application-form/download/student/{internshipId}")
    public HttpEntity<byte[]> downloadApplicationFormByStudent(@PathVariable("internshipId") String internshipId) {
        Document document = internshipService.getApplicationFormByStudent(internshipId);
        return downloadHelper.fromDocument(document);
    }

    /** Get Application Form By Firm */
    @GetMapping(path = "/application-form/download/firm/{internshipId}")
    public HttpEntity<byte[]> downloadApplicationFormByFirm(@PathVariable("internshipId") String internshipId) {
        Document document = internshipService.getApplicationFormByFirm(internshipId);
        return downloadHelper.fromDocument(document);
    }

    /** Record that no SSI transactions will take place. */
    @PatchMapping(path = "/ssi/set-no-insurance/{internshipId}")
    public ResponseEntity<HttpStatus> setNoInsurance(@PathVariable("internshipId") String internshipId) {
        internshipService.setNoInsurance(internshipId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Record that SSI transactions are delegated to the Department Secretary. */
    @PatchMapping(path = "/ssi/set-handler-to-department-secretary/{internshipId}")
    public ResponseEntity<HttpStatus> setHandlerToDepartmentSecretary(@PathVariable("internshipId") String internshipId) {
        internshipService.setHandlerToDepartmentSecretary(internshipId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Record that SSI transactions are delegated to the Deans's Office. */
    @PatchMapping(path = "/ssi/set-handler-to-deans-office/{internshipId}")
    public ResponseEntity<HttpStatus> setHandlerToDeansOffice(@PathVariable("internshipId") String internshipId) {
        internshipService.setHandlerToDeansOffice(internshipId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Send Employment Document */
    @PostMapping(path = "/employment-document/send/{internshipId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> sendEmploymentDocument (
            @PathVariable("internshipId") String internshipId,
            @RequestPart("file") MultipartFile file) {
        internshipService.sendEmploymentDocument(internshipId, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Download Employment Document */
    @GetMapping(path = "/employment-document/download/{internshipId}")
    public HttpEntity<byte[]> downloadEmploymentDocument(@PathVariable("internshipId") String internshipId) {
        Document document = internshipService.getEmploymentDocument(internshipId);
        return downloadHelper.fromDocument(document);
    }

    /** Send Summer Practice Report */
    @PostMapping(path = "/report/send/{internshipId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> sendSummerPracticeReport (
            @PathVariable("internshipId") String internshipId,
            @RequestPart("file") MultipartFile file) {
        internshipService.sendSummerPracticeReport(internshipId, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Evaluate Summer Practice Report */
    @PatchMapping(path = "/report/evaluate/{internshipId}")
    public ResponseEntity<HttpStatus> updateSummerPracticeReportAcceptance(
            @PathVariable("internshipId") String internshipId,
            UpdateDocumentAcceptanceRequest request) {
        internshipService.updateSummerPracticeReportAcceptance(internshipId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Download Summer Practice Report */
    @GetMapping(path = "/report/download/{internshipId}")
    public HttpEntity<byte[]> downloadSummerPracticeReport(@PathVariable("internshipId") String internshipId) {
        Document document = internshipService.getSummerPracticeReport(internshipId);
        return downloadHelper.fromDocument(document);
    }

    /** Send Company Form */
    @PostMapping(path = "/company-form/send/{internshipId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> sendCompanyForm (
            @PathVariable("internshipId") String internshipId,
            @RequestPart("file") MultipartFile file) {
        internshipService.sendCompanyForm(internshipId, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Download Company Form */
    @GetMapping(path = "/company-form/download/{internshipId}")
    public HttpEntity<byte[]> downloadCompanyForm(@PathVariable("internshipId") String internshipId) {
        Document document = internshipService.getCompanyForm(internshipId);
        return downloadHelper.fromDocument(document);
    }

    /** Send Survey */
    @PostMapping(path = "/survey/send/{internshipId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> sendSurvey (
            @PathVariable("internshipId") String internshipId,
            @RequestPart("file") MultipartFile file) {
        internshipService.sendSurvey(internshipId, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Download Survey */
    @GetMapping(path = "/survey/download/{internshipId}")
    public HttpEntity<byte[]> downloadSurvey(@PathVariable("internshipId") String internshipId) {
        Document document = internshipService.getSurvey(internshipId);
        return downloadHelper.fromDocument(document);
    }
}
