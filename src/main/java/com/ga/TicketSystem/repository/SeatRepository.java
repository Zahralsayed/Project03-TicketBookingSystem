package com.ga.TicketSystem.repository;

import com.ga.TicketSystem.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
