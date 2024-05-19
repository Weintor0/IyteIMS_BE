package edu.iyte.ceng.internship.ims.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String id;
    private String token;
}
