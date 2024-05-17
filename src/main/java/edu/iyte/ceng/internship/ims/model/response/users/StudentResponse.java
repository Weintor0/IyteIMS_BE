package edu.iyte.ceng.internship.ims.model.response.users;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class StudentResponse {
    private UserResponse user;
    private String id;
    private String studentNumber;
    private String name;
    private String surname;
    private Date birthDate ;
}
