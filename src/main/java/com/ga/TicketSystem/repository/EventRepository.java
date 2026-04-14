package com.ga.TicketSystem.repository;

import com.ga.TicketSystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByLocationId(Long locationId);
}
