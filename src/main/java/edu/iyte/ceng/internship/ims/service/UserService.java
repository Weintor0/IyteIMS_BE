package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(String email, String password, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setUserRole(role);
        return userRepository.save(user);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing, userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing, email)
        );
    }

    public User updateUser(String userId, String email, String password) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing, userId));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentEmail.equals(user.getEmail())) {
            throw new BusinessException(ErrorCode.Unauthorized, "Users can only update their own accounts.");
        }

        String newEmail = email != null ? email : user.getEmail();
        String newPassword = password != null ? bCryptPasswordEncoder.encode(password) : user.getPassword();

        user.setEmail(newEmail);
        user.setPassword(newPassword);
        
        return userRepository.save(user);
    }
}
