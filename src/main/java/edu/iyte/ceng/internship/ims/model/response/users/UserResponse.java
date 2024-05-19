package edu.iyte.ceng.internship.ims.model.response.users;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private String id;
    private String email;
}
