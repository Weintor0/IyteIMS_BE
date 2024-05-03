package edu.iyte.ceng.internship.ims.model.request;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Student;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Student.entityName)
public class UpdateStudentRequest {
    private String email;
    private String password;
}
