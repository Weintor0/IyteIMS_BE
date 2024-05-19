package edu.iyte.ceng.internship.ims.model.request;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = User.entityName)
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "Email must be well-formed.")
    @NotBlank(message = "Email cannot be blank.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
