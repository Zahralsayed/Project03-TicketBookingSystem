package com.ga.TicketSystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long bookingId;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private String username; // Just the name, not the whole User object
    private List<TicketDTO> tickets;
}

