package edu.iyte.ceng.internship.ims.model.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Firm;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Firm.entityName)
public class CreateFirmRequest {
    // TODO
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date registerDate;

    private String firmName;
    private String typeOfBusiness;
    private String businessRegistrationNumber;
    private String legalStructure;
    private String phoneNumber;
    private String address;
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
