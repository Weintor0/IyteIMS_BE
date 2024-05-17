package edu.iyte.ceng.internship.ims.model.request.users;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Student;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Student.entityName)
public class StudentRegisterRequest {
    @NotBlank(message = "Student number cannot be blank.")
    private String studentNumber;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date birthDate;

    @NotBlank(message = "Name cannot be blank.")
    private String name;

    @NotBlank(message = "Surname cannot be blank.")
    private String surname;

    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
