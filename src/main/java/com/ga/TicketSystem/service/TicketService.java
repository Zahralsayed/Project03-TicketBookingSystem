package com.ga.TicketSystem.service;

import com.ga.TicketSystem.model.Event;
import com.ga.TicketSystem.model.Seat;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    public double calculateTicketPrice(Event event, Seat seat) {
        Double base = event.getBasePrice();
        Double multiplier = seat.getCategory().getMultiplier();

        return base * multiplier;
    }
}
