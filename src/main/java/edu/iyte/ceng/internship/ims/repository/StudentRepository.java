package edu.iyte.ceng.internship.ims.repository;

import java.util.Optional;

import edu.iyte.ceng.internship.ims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.iyte.ceng.internship.ims.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findStudentByStudentNumber(String number);

    //Optional<Student> findStudentById(String id);

    Optional<Student> findStudentByUser(User user);
}
