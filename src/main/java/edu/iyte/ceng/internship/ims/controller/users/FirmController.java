package edu.iyte.ceng.internship.ims.controller.users;

import edu.iyte.ceng.internship.ims.model.response.users.FirmResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.model.request.users.UpdateFirmRequest;
import edu.iyte.ceng.internship.ims.service.FirmService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/firm")
public class FirmController {
    private FirmService firmService;

    @PutMapping("/update/{userId}")
    public ResponseEntity<FirmResponse> updateFirm(@PathVariable("userId") String userId, @Valid @RequestBody UpdateFirmRequest updateRequest) {
        FirmResponse response = firmService.updateFirm(userId, updateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<FirmResponse> getFirm(@PathVariable("userId") String userId) {
        FirmResponse response = firmService.getFirm(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
