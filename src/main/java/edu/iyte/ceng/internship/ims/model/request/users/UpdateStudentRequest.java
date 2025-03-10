package edu.iyte.ceng.internship.ims.model.request.users;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Student;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Student.entityName)
public class UpdateStudentRequest {
    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email must be well-formed.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}
