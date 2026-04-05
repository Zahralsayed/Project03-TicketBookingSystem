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
}
