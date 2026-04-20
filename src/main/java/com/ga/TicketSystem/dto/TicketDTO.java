package com.ga.TicketSystem.dto;

import lombok.Data;

@Data
public class TicketDTO {
    private Long ticketId;
    private String eventName;
    private String seatNumber;
    private String uniqueHash;
    private double price;
}
