package com.ga.TicketSystem.repository;

import com.ga.TicketSystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
