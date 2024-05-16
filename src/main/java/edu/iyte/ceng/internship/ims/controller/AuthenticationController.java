package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.model.request.CreateFirmRequest;
import edu.iyte.ceng.internship.ims.model.request.CreateStudentRequest;
import edu.iyte.ceng.internship.ims.model.request.LoginRequest;
import edu.iyte.ceng.internship.ims.service.AuthenticationService;
import edu.iyte.ceng.internship.ims.service.FirmService;
import edu.iyte.ceng.internship.ims.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private AuthenticationService authenticationService;
    private StudentService studentService;
    private FirmService firmService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/register/firm")
    public ResponseEntity<String> createFirm(@Valid @RequestBody CreateFirmRequest createFirmRequest) {
        firmService.createFirm(createFirmRequest);
        ResponseEntity<String> response = authenticationService.login(
                new LoginRequest(createFirmRequest.getEmail(), createFirmRequest.getPassword()));
        return new ResponseEntity<>(response.getBody(), response.getHeaders(), HttpStatus.CREATED);
    }

    @PostMapping("/register/student")
    public ResponseEntity<String> createStudent(@Valid @RequestBody CreateStudentRequest createRequest) {
        studentService.createStudent(createRequest);
        ResponseEntity<String> response = authenticationService.login(
                new LoginRequest(createRequest.getEmail(), createRequest.getPassword()));
        return new ResponseEntity<>(response.getBody(), response.getHeaders(), HttpStatus.CREATED);
    }
}
