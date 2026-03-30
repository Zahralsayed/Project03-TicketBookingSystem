package com.ga.TicketSystem.model;


import com.ga.TicketSystem.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;
    private BookingStatus bookingStatus;

    @Column(updatable = false)
    private LocalDate createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Ticket> tickets;
}
