package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.model.request.LoginRequest;
import edu.iyte.ceng.internship.ims.service.AuthenticationService;
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
    private AuthenticationService authenticationService;

    @PostMapping("/register/firm")
    public ResponseEntity<String> createFirm(@Valid @RequestBody CreateFirmRequest createFirmRequest) {
        firmService.createFirm(createFirmRequest);
        ResponseEntity<String> response = authenticationService.login(
                new LoginRequest(createFirmRequest.getEmail(), createFirmRequest.getPassword()));
        return new ResponseEntity<>(response.getBody(), response.getHeaders(), HttpStatus.CREATED);
    }

    @PutMapping("/update-firm-account/{userId}")
    public ResponseEntity<Firm> updateFirm(@PathVariable("userId") String userId, @Valid @RequestBody UpdateFirmRequest updateRequest) {
        Firm firm = firmService.updateFirm(Long.parseLong(userId), updateRequest);
        return new ResponseEntity<>(firm, HttpStatus.OK);
    }

    @GetMapping("/get-firm-account/{userId}")
    public ResponseEntity<Firm> getFirm(@PathVariable("userId") String userId) {
        Firm firm = firmService.getFirm(Long.parseLong(userId));
        return new ResponseEntity<>(firm, HttpStatus.OK);
    }
}
