package edu.iyte.ceng.internship.ims.model.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CreateFirmRequest {
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date registerDate;

    private String firmName;
    private String typeOfBusiness;
    private String businessRegistrationNumber;
    private String legalStructure;
    private String phoneNumber;
    private String address;
    private String email;
    private String password;
}
