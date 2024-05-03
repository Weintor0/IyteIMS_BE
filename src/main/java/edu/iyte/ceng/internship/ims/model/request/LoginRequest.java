package edu.iyte.ceng.internship.ims.model.request;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.User;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = User.entityName)
public class LoginRequest {
    private String email;
    private String password;
}
