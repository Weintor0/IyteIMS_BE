package edu.iyte.ceng.internship.ims.controller.users;

import edu.iyte.ceng.internship.ims.model.request.users.UpdateFirmRequest;
import edu.iyte.ceng.internship.ims.model.response.users.FirmResponse;
import edu.iyte.ceng.internship.ims.service.AuthenticationService;
import edu.iyte.ceng.internship.ims.service.FirmService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/firm")
public class FirmController {
    private FirmService firmService;
    private AuthenticationService authenticationService;

    @PutMapping("/update/{userId}")
    public ResponseEntity<FirmResponse> updateFirm(@PathVariable("userId") String userId, @Valid @RequestBody UpdateFirmRequest updateRequest) {
        FirmResponse response = firmService.updateFirm(userId, updateRequest, authenticationService.getCurrentUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<FirmResponse> getFirm(@PathVariable("userId") String userId) {
        FirmResponse response = firmService.getFirm(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
