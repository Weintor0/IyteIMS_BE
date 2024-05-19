package edu.iyte.ceng.internship.ims.service.mapper;

import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.model.request.users.StudentRegisterRequest;
import edu.iyte.ceng.internship.ims.model.response.users.StudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentMapper {
    private final UserMapper userMapper;

    @Autowired
    public StudentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public StudentResponse fromEntity(Student student) {
        StudentResponse response = StudentResponse.builder()
                .user(userMapper.fromUser(student.getUser()))
                .studentNumber(student.getStudentNumber())
                .name(student.getName())
                .surname(student.getSurname())
                .birthDate(student.getBirthDate())
                .build();
        return response;
    }

    public Student fromRequest(StudentRegisterRequest studentRegisterRequest, User user) {
        Student student = new Student();
        student.setStudentNumber(studentRegisterRequest.getStudentNumber());
        student.setName(studentRegisterRequest.getName());
        student.setSurname(studentRegisterRequest.getSurname());
        student.setBirthDate(studentRegisterRequest.getBirthDate());
        student.setUser(user);
        return student;
    }
}
