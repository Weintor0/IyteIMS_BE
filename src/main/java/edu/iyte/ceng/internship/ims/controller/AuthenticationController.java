package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.model.request.users.FirmRegisterRequest;
import edu.iyte.ceng.internship.ims.model.request.users.StudentRegisterRequest;
import edu.iyte.ceng.internship.ims.model.request.LoginRequest;
import edu.iyte.ceng.internship.ims.model.response.LoginResponse;
import edu.iyte.ceng.internship.ims.service.AuthenticationService;
import edu.iyte.ceng.internship.ims.service.FirmService;
import edu.iyte.ceng.internship.ims.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", loginResponse.getToken());
        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }

    @PostMapping("/register/firm")
    public ResponseEntity<LoginResponse> createFirm(@Valid @RequestBody FirmRegisterRequest firmRegisterRequest) {
        authenticationService.registerFirm(firmRegisterRequest);
        LoginResponse loginResponse = authenticationService.login(
                new LoginRequest(firmRegisterRequest.getEmail(), firmRegisterRequest.getPassword()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", loginResponse.getToken());
        return new ResponseEntity<>(loginResponse, headers, HttpStatus.CREATED);
    }

    @PostMapping("/register/student")
    public ResponseEntity<LoginResponse> createStudent(@Valid @RequestBody StudentRegisterRequest createRequest) {
        studentService.createStudent(createRequest);
        LoginResponse loginResponse = authenticationService.login(
                new LoginRequest(createRequest.getEmail(), createRequest.getPassword()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", loginResponse.getToken());
        return new ResponseEntity<>(loginResponse, headers, HttpStatus.CREATED);
    }
}
