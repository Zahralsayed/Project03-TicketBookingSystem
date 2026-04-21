package com.ga.TicketSystem.controller;

import com.ga.TicketSystem.model.request.LoginRequest;
import com.ga.TicketSystem.model.User;
import com.ga.TicketSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        System.out.println("Calling registerUser ==> ");
        try {
            userService.register(user);
            return ResponseEntity.ok("Registration successful! Please check your email to verify.");
        }
        catch (Exception e) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token) {
        boolean isVerified  = userService.verifyUser(token);

        if (isVerified) {
            return "Account Verified Successfully";
        } else {
            return "Invalid or Expired Token";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Calling login ==> ");
//        try {
           return userService.login(loginRequest);
//            return ResponseEntity.ok(user);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

    }

    @PostMapping("/change-password")
    public String change(@RequestParam Long userId, @RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return "Password updated successfully!";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok("Reset link has been sent.");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully. You can now login.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/delete/{Id}")
    public ResponseEntity<?> delete(@PathVariable("Id") Long Id) {
        try {
            userService.deleteUser(Id);
            String message = userService.deleteUser(Id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
