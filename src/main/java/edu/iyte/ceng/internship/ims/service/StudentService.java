package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.model.response.users.StudentResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.entity.User;
//import edu.iyte.ceng.internship.ims.entity.UserRole; // TODO
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.users.StudentRegisterRequest;
import edu.iyte.ceng.internship.ims.model.request.users.UpdateStudentRequest;
import edu.iyte.ceng.internship.ims.repository.StudentRepository;
import edu.iyte.ceng.internship.ims.service.mapper.StudentMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentService {
    private StudentRepository studentRepository;
    private UserService userService;
    private StudentMapper studentMapper;

    @Transactional(rollbackFor = Exception.class)
    public StudentResponse createStudent(StudentRegisterRequest createRequest) {
        User user = userService.createUser(
            createRequest.getEmail(), 
            createRequest.getPassword(),
            UserRole.Student);

        Student student = studentMapper.fromRequest(createRequest, user);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.fromEntity(savedStudent);
    }

    public StudentResponse getStudent(String userId) {
        User user = userService.getUserById(userId);
        Student student = studentRepository.findStudentByUser(user).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing,
            "Student with User ID " + userId + " does not exist")
        );

        ensureReadPrivilege(userId);
        return studentMapper.fromEntity(student);
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentResponse updateStudent(String userId, UpdateStudentRequest updateRequest) {
        userService.updateUser(
            userId, 
            updateRequest.getEmail(), 
            updateRequest.getPassword()
        );

        Student updatedStudent = studentRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User disappeared within a single transaction.")
        );
        return studentMapper.fromEntity(updatedStudent);
    }

    private void ensureReadPrivilege(String userId) {
        String currentId = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserById(currentId);

        switch (currentUser.getUserRole()) {
            case Student:
                if (!currentUser.getId().equals(userId)) {
                    throw new BusinessException(ErrorCode.Forbidden,
                    "Students can only access their own profile information.");
                }
                break;
            case Firm:
                // TODO: The required logic will be added after implementing internship.
                throw new BusinessException(ErrorCode.Forbidden,
                "Firms can only access the information of students working within their company.");
            default:
                break;
        }
    }
}
