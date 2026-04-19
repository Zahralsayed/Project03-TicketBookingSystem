package com.ga.TicketSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private LocalDateTime endTime;
    private Double basePrice;
//    private String eventImageUrl; // Admin can upload event posters

    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonIgnore
    private Location location;
}
