package edu.iyte.ceng.internship.ims.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.model.request.CreateFirmRequest;
import edu.iyte.ceng.internship.ims.model.request.UpdateFirmRequest;
import edu.iyte.ceng.internship.ims.service.FirmService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class FirmController {
    private FirmService firmService;

    @PostMapping("/register/firm")
    public ResponseEntity<String> createFirm(@Valid @RequestBody CreateFirmRequest createFirmRequest) {
        String userId = firmService.createFirm(createFirmRequest).toString();
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @PutMapping("/update-firm-account/{userId}")
    public ResponseEntity<Firm> updateFirm(@PathVariable("userId") String userId, @RequestBody UpdateFirmRequest updateRequest) {
        Firm firm = firmService.updateFirm(Long.parseLong(userId), updateRequest);
        return new ResponseEntity<>(firm, HttpStatus.OK);
    }

    @GetMapping("/get-firm-account/{userId}")
    public ResponseEntity<Firm> getFirm(@PathVariable("userId") String userId) {
        Firm firm = firmService.getFirm(Long.parseLong(userId));
        return new ResponseEntity<>(firm, HttpStatus.OK);
    }
}
