package edu.iyte.ceng.internship.ims.model.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Student;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Student.entityName)
public class CreateStudentRequest {
    private String studentNumber;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date birthDate;

    private String name;
    private String surname;
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
