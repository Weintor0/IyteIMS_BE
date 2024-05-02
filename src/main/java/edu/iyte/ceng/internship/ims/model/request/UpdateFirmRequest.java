package edu.iyte.ceng.internship.ims.model.request;

import lombok.Data;

@Data
public class UpdateFirmRequest {
    private String firmName;
    private String phoneNumber;
    private String address;
    private String email;
    private String password;
}
