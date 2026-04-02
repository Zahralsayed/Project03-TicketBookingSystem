package com.ga.TicketSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;
    private String description;
    private LocalDateTime startTime;
//    private String eventImageUrl; // Admin can upload event posters

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
