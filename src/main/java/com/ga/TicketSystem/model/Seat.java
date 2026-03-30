package com.ga.TicketSystem.model;

import com.ga.TicketSystem.enums.SeatStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
@Table(name= "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rowNumber;
    private String seatNumber;
    private SeatStatus status;

    @Version
    private Integer version; // Automatic Optimistic Locking

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
