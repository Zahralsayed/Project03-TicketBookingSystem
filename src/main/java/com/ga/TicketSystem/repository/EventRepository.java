package com.ga.TicketSystem.repository;

import com.ga.TicketSystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
