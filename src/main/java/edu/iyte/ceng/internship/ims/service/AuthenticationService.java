package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.LoginRequest;
import edu.iyte.ceng.internship.ims.entity.User;

import edu.iyte.ceng.internship.ims.model.response.LoginResponse;
import edu.iyte.ceng.internship.ims.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PasswordMismatch, "Incorrect password");
        }

        return LoginResponse.builder()
                .id(user.getId().toString())
                .token(jwtService.createToken(user.getId().toString(), user.getUserRole()))
                .build();
    }
}
