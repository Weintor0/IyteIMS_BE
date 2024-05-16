package edu.iyte.ceng.internship.ims.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iyte.ceng.internship.ims.entity.User;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByEmail(String email);
}
