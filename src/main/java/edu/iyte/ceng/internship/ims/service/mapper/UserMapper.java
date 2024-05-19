package edu.iyte.ceng.internship.ims.service.mapper;

import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.model.response.users.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }
}
