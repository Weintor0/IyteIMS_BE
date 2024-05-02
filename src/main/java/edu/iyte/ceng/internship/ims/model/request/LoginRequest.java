package edu.iyte.ceng.internship.ims.model.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
