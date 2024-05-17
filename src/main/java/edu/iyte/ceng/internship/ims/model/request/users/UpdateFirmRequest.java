package edu.iyte.ceng.internship.ims.model.request.users;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Firm;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Firm.entityName)
public class UpdateFirmRequest {
    @NotBlank(message = "Firm name cannot be blank.")
    private String firmName;

    @NotBlank(message = "Phone number cannot be blank.")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank.")
    private String address;

    @NotBlank(message = "Email cannot be blank.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
