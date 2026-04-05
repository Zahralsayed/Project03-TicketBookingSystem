package com.ga.TicketSystem.controller;

import com.ga.TicketSystem.model.User;
import com.ga.TicketSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        System.out.println("Calling registerUser ==> ");
        userService.register(user);
        return ResponseEntity.ok("Registration successful! Please check your email to verify.");

    }

    @GetMapping("/auth/verify")
    public String verifyAccount(@RequestParam("token") String token) {
        boolean isVerified  = userService.verifyUser(token);

        if (isVerified) {
            return "Account Verified Successfully";
        } else {
            return "Invalid or Expired Token";
        }
    }
}
