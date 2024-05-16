package edu.iyte.ceng.internship.ims.controller.users;

import edu.iyte.ceng.internship.ims.model.request.LoginRequest;
import edu.iyte.ceng.internship.ims.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.model.request.CreateStudentRequest;
import edu.iyte.ceng.internship.ims.model.request.UpdateStudentRequest;
import edu.iyte.ceng.internship.ims.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class StudentController {
    private StudentService studentService;

    @PutMapping("/update-student-account/{userId}")
    public ResponseEntity<Student> updateStudent(@PathVariable("userId") String userId, @Valid @RequestBody UpdateStudentRequest updateStudent) {
        Student student = studentService.updateStudent(userId, updateStudent);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping("/get-student-account/{userId}")
    public ResponseEntity<Student> getStudent(@PathVariable("userId") String userId) {
        Student student = studentService.getStudent(userId);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }
    
}
