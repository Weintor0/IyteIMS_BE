package edu.iyte.ceng.internship.ims.model.request;

import lombok.Data;

@Data
public class UpdateStudentRequest {
    private String email;
    private String password;
}
