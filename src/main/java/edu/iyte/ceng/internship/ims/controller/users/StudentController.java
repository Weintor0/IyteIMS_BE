package edu.iyte.ceng.internship.ims.controller.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.model.request.users.UpdateStudentRequest;
import edu.iyte.ceng.internship.ims.service.StudentService;
import edu.iyte.ceng.internship.ims.model.response.users.StudentResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@AllArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {
    private StudentService studentService;

    @PutMapping("/update/{userId}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable("userId") String userId, @Valid @RequestBody UpdateStudentRequest updateStudent) {
        StudentResponse response = studentService.updateStudent(userId, updateStudent);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable("userId") String userId) {
        StudentResponse response = studentService.getStudent(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}
