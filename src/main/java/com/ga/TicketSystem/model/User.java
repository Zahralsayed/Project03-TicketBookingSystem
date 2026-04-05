package com.ga.TicketSystem.model;

import com.ga.TicketSystem.enums.Role;
import com.ga.TicketSystem.enums.UserStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @Column(unique = true)
    private String email;
    private String verificationToken;
    private String password_hash;
    private Role role;
    private UserStatus status;
    private boolean isVerified = false;
    private String profilePicture;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;
}