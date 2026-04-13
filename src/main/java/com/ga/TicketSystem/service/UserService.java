package com.ga.TicketSystem.service;

import com.ga.TicketSystem.enums.UserStatus;
import com.ga.TicketSystem.model.User;
import com.ga.TicketSystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public User register(User user) {

        // Generate the unique token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerified(false);
        user.setStatus(UserStatus.PENDING);

        User savedUser = userRepository.save(user);

        // Send the email
        this.emailService.sendVerificationLink(savedUser.getEmail(), savedUser.getUsername(), token);

        return savedUser;
    }

    @Transactional
    public boolean verifyUser(String token) {
        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    user.setVerified(true);
                    user.setStatus(UserStatus.ACTIVE);
                    user.setVerificationToken(null); // Clear, "one-time" use
                    userRepository.save(user);
                    return true;
                }).orElse(false);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword_hash().equals(password)) {
            throw new RuntimeException("Invalid Credentials");
        }

        if (!user.isVerified()){
            throw new RuntimeException("Account not verified, Please check your email.");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Your Account is "+ user.getStatus() +", Please contact support.");
        }

        return user;
    }

    public void changePassword(Long userId, String oldPassword ,String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

       if (user.getStatus().equals(UserStatus.ACTIVE)) {

           if (!user.getPassword_hash().equals(oldPassword)) {
               throw new RuntimeException("The old password is incorrect");
           }

           user.setPassword_hash(newPassword);
           userRepository.save(user);

       } else { throw new RuntimeException("You Can't Change Password For " + user.getStatus()+ " Account.");}

    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        emailService.sendResetLink(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset link."));

        user.setPassword_hash(newPassword);
        user.setResetToken(null);
        userRepository.save(user);
    }

}
