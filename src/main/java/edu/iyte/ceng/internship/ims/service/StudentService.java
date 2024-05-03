package edu.iyte.ceng.internship.ims.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.BusinessExceptionType;
import edu.iyte.ceng.internship.ims.model.request.CreateStudentRequest;
import edu.iyte.ceng.internship.ims.model.request.UpdateStudentRequest;
import edu.iyte.ceng.internship.ims.repository.StudentRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentService {
    private StudentRepository studentRepository;
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public Long createStudent(CreateStudentRequest createRequest) {
        User user = userService.createUser(
            UserRole.Student, 
            createRequest.getEmail(), 
            createRequest.getPassword());

        Student student = new Student(
            user.getUserId(),
            user,
            createRequest.getStudentNumber(),
            createRequest.getBirthDate(),
            createRequest.getName(),
            createRequest.getSurname());

        return studentRepository.save(student).getUser().getUserId();
    }

    public Student getStudent(Long userId) {
        Student student = studentRepository.findStudentById(userId).orElseThrow(
            () -> new BusinessException(BusinessExceptionType.AccountMissing, 
            "Student with User ID " + userId + " does not exist")
        );

        ensureReadPrivilege(userId);
        return student;
    }

    public Student updateStudent(Long userId, UpdateStudentRequest updateRequest) {
        userService.updateUser(
            userId, 
            updateRequest.getEmail(), 
            updateRequest.getPassword()
        );

        return studentRepository.findById(userId).get();
    }

    private void ensureReadPrivilege(Long userId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserByEmail(currentEmail);

        switch (currentUser.getUserRole()) {
            case Student:
                if (!currentUser.getUserId().equals(userId)) {
                    throw new BusinessException(BusinessExceptionType.Forbidden, 
                    "Students can only access their own profile information.");
                }
                break;
            case Firm:
                // TODO
                throw new BusinessException(BusinessExceptionType.Forbidden, 
                "Firms can only access the information of students working within their company.");
            default:
                break;
        }
    }
}
