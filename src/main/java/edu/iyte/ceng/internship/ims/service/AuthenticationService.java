package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.LoginRequest;
import edu.iyte.ceng.internship.ims.entity.User;

import edu.iyte.ceng.internship.ims.model.request.users.FirmRegisterRequest;
import edu.iyte.ceng.internship.ims.model.response.LoginResponse;
import edu.iyte.ceng.internship.ims.model.response.users.FirmResponse;
import edu.iyte.ceng.internship.ims.repository.FirmRepository;
import edu.iyte.ceng.internship.ims.repository.UserRepository;
import edu.iyte.ceng.internship.ims.security.JwtService;
import edu.iyte.ceng.internship.ims.service.mapper.FirmMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private FirmRepository firmRepository;
    private FirmMapper firmMapper;
    private UserService user ;

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

    @Transactional(rollbackFor = Throwable.class)
    public FirmResponse registerFirm(FirmRegisterRequest createRequest) {
        User user = userService.createUser(
                createRequest.getEmail(),
                createRequest.getPassword(),
                UserRole.Firm);

        Firm firm = firmMapper.fromRequest(createRequest, user);
        Firm createdFirm = firmRepository.save(firm);
        return firmMapper.fromEntity(createdFirm);
    }

    public User getCurrentUser() {
        return user.getUserById(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
