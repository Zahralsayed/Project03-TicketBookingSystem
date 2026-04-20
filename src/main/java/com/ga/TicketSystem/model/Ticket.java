package com.ga.TicketSystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tickets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "seat_id"})})
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price = 0.0;

    @Column(unique = true)
    private String uniqueHash; // Digital signature/QR code data

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
}
