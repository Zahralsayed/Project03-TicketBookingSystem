package com.ga.TicketSystem.service;

import com.ga.TicketSystem.model.request.LoginRequest;
import com.ga.TicketSystem.enums.UserStatus;
import com.ga.TicketSystem.model.User;
import com.ga.TicketSystem.repository.UserRepository;
import com.ga.TicketSystem.security.JWTUtils;
import com.ga.TicketSystem.security.MyUserDetails;
import com.ga.TicketSystem.security.MyUserDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public UserService(UserRepository userRepository,
                       EmailService emailService,
                       @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetailsService myUserDetailsService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = myUserDetailsService;
//        this.tokenService = tokenService;
    }

    @Transactional
    public User register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already registered!");
        }

        user.setPassword_hash(passwordEncoder.encode(user.getPassword_hash()));

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

public ResponseEntity<?> login(LoginRequest loginRequest) {
    System.out.println("Service Calling loginUser ==> ");
    try {
        // Authenticate using email + password
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // Load UserDetails by email
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
        MyUserDetails myUser = (MyUserDetails) userDetails;

        // Check if account is active
        if (myUser.getUser().getStatus() != UserStatus.ACTIVE) {
            User user = myUser.getUser();
            String token = UUID.randomUUID().toString();
//
//            user.setVerificationToken(token);
//            user.setVerificationTokenDate(LocalDateTime.now());
            userRepository.save(user);

//            sendVerificationEmail(myUser.getUser(), token);

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "Your account is inactive. Please verify your email.",
                            "verificationUrl", "http://localhost:8080/auth/users/verify?token=" + token
                    ));
        }

        // Generate JWT
        String jwt = jwtUtils.generateToken(userDetails);

        // Return login success
        return ResponseEntity.ok(
                Map.of(
                        "email", userDetails.getUsername(),
                        "username", myUser.getUser().getUsername(),
                        "roles", userDetails.getAuthorities(),
                        "token", jwt
                )
        );

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password!"));
    }
}


    public void changePassword(Long userId, String oldPassword ,String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

       if (user.getStatus().equals(UserStatus.ACTIVE)) {

           if (!passwordEncoder.matches(oldPassword, user.getPassword_hash())){
               throw new RuntimeException("The old password is incorrect");
           }

           user.setPassword_hash(passwordEncoder.encode(newPassword));
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

        user.setPassword_hash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}
