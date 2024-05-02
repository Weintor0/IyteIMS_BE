package edu.iyte.ceng.internship.ims.model.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CreateStudentRequest {
    private String studentNumber;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date birthDate;

    private String name;
    private String surname;
    private String email;
    private String password;
}
