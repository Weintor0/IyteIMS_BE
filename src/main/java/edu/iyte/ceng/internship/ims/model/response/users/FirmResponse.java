package edu.iyte.ceng.internship.ims.model.response.users;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Date;

@Builder
@Data
public class FirmResponse {
    private UserResponse user;
    private ZonedDateTime registerDate;
    private String firmName;
    private String typeOfBusiness;
    private String businessRegistrationNumber;
    private String legalStructure;
    private String phoneNumber;
    private String address;
}
