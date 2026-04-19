package com.ga.TicketSystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ga.TicketSystem.enums.SeatCategory;
import com.ga.TicketSystem.enums.SeatStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name= "seats")
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rowNumber;
    private String seatNumber;
    private SeatStatus status;
    private SeatCategory category;

    @Version
    private Integer version; // Automatic Optimistic Locking

    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonBackReference
    private Location location;
}
