package edu.iyte.ceng.internship.ims.model.request.users;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Firm;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Firm.entityName)
public class FirmRegisterRequest {

    @NotBlank(message = "Firm name cannot be empty.")
    private String firmName;

    @NotBlank(message = "Type of business cannot be empty.")
    private String typeOfBusiness;

    @NotBlank(message = "Business registration number cannot be empty.")
    private String businessRegistrationNumber;

    @NotBlank(message = "Legal structure cannot be empty.")
    private String legalStructure;

    @NotBlank(message = "Phone number cannot be empty.")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be empty.")
    private String address;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email must be well-formed.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
