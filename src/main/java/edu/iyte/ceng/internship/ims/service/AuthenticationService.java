package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.LoginRequest;
import edu.iyte.ceng.internship.ims.entity.User;

import edu.iyte.ceng.internship.ims.model.request.users.FirmRegisterRequest;
import edu.iyte.ceng.internship.ims.model.request.users.StudentRegisterRequest;
import edu.iyte.ceng.internship.ims.model.response.LoginResponse;
import edu.iyte.ceng.internship.ims.model.response.users.FirmResponse;
import edu.iyte.ceng.internship.ims.model.response.users.StudentResponse;
import edu.iyte.ceng.internship.ims.repository.FirmRepository;
import edu.iyte.ceng.internship.ims.repository.StudentRepository;
import edu.iyte.ceng.internship.ims.repository.UserRepository;
import edu.iyte.ceng.internship.ims.security.JwtService;
import edu.iyte.ceng.internship.ims.service.mapper.FirmMapper;
import edu.iyte.ceng.internship.ims.service.mapper.StudentMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final FirmRepository firmRepository;
    private final FirmMapper firmMapper;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail());
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PasswordMismatch, "Incorrect password");
        }

        return LoginResponse.builder()
                .id(user.getId())
                .token(jwtService.createToken(user.getId(), user.getUserRole()))
                .build();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void registerFirm(FirmRegisterRequest createRequest) {
        User user = userService.createUser(
                createRequest.getEmail(),
                createRequest.getPassword(),
                UserRole.Firm);

        Firm firm = firmMapper.fromRequest(createRequest, user);
        firmRepository.save(firm);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void registerStudent(StudentRegisterRequest createRequest) {
        User user = userService.createUser(
                createRequest.getEmail(),
                createRequest.getPassword(),
                UserRole.Student);

        Student student = studentMapper.fromRequest(createRequest, user);
        studentRepository.save(student);
    }

    public User getCurrentUser() {
        return userService.getUserById(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public AuthenticationService doIfUserIsStudent(Consumer<Student> consumer) {
        User user = getCurrentUser();
        if (user.getUserRole() == UserRole.Student) {
            Student student = studentRepository.findStudentByUser(user).orElseThrow(
                    () -> new BusinessException(ErrorCode.AccountMissing, "Student account does not exist."));
            consumer.accept(student);
        }

        return this;
    }

    public AuthenticationService doIfUserIsFirm(Consumer<Firm> consumer) {
        User user = getCurrentUser();
        if (user.getUserRole() == UserRole.Firm) {
            Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                    () -> new BusinessException(ErrorCode.AccountMissing, "Firm account does not exist."));
            consumer.accept(firm);
        }

        return this;
    }

    public AuthenticationService doIfUserIsInternshipCoordinator(Consumer<User> consumer) {
        User user = getCurrentUser();
        if (user.getUserRole() == UserRole.InternshipCoordinator) {
            consumer.accept(user);
        }

        return this;
    }

    public AuthenticationService doIfUserIsDepartmentSecretary(Consumer<User> consumer) {
        User user = getCurrentUser();
        if (user.getUserRole() == UserRole.DepartmentSecretary) {
            consumer.accept(user);
        }

        return this;
    }

    public <T> T doIfUserIsStudentOrElseThrow(Function<Student, T> function, Supplier<BusinessException> exceptionSupplier) {
        User user = getCurrentUser();
        if (user.getUserRole() == UserRole.Student) {
            Student student = studentRepository.findStudentByUser(user).orElseThrow(
                    () -> new BusinessException(ErrorCode.AccountMissing, "Student account does not exist."));
            return function.apply(student);
        } else {
            throw exceptionSupplier.get();
        }
    }

    public <T> T doIfUserIsFirmOrElseThrow(Function<Firm, T> function, Supplier<BusinessException> exceptionSupplier) {
        User user = getCurrentUser();
        if (getCurrentUser().getUserRole() == UserRole.Firm) {
            Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                    () -> new BusinessException(ErrorCode.AccountMissing, "Firm account does not exist."));
            return function.apply(firm);
        } else {
            throw exceptionSupplier.get();
        }
    }

    public <T> T doIfUserIsInternshipCoordinatorOrElseThrow(Function<User, T> function, Supplier<BusinessException> exceptionSupplier) {
        User user = getCurrentUser();
        if (user.getUserRole() == UserRole.InternshipCoordinator) {
            return function.apply(user);
        } else {
            throw exceptionSupplier.get();
        }
    }

    public <T> T doIfUserIsDepartmentSecretaryOrElseThrow(Function<User, T> function, Supplier<BusinessException> exceptionSupplier) {
        User user = getCurrentUser();
        if (user.getUserRole() == UserRole.DepartmentSecretary) {
            return function.apply(user);
        } else {
            throw exceptionSupplier.get();
        }
    }
}
