package edu.iyte.ceng.internship.ims.model.request.users;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Firm;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Firm.entityName)
public class FirmRegisterRequest {
    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotNull(message = "Register date cannot be empty.")
    private Date registerDate;

    @NotEmpty(message = "Firm name cannot be empty.")
    private String firmName;

    @NotEmpty(message = "Type of business cannot be empty.")
    private String typeOfBusiness;

    @NotEmpty(message = "Business registration number cannot be empty.")
    private String businessRegistrationNumber;

    @NotEmpty(message = "Legal structure cannot be empty.")
    private String legalStructure;

    @NotEmpty(message = "Phone number cannot be empty.")
    private String phoneNumber;

    @NotEmpty(message = "Address cannot be empty.")
    private String address;

    @NotEmpty(message = "Email must be well-formed.")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
