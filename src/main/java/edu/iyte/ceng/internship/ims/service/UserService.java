package edu.iyte.ceng.internship.ims.service;

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

    public User createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing, userId.toString()));
        return user;
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing, email)
        );
        return user;
    }

    public User updateUser(Long userId, String email, String password) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing, userId.toString()));

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
