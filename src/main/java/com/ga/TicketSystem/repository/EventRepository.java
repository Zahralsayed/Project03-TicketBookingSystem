package com.ga.TicketSystem.repository;

import com.ga.TicketSystem.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByLocationId(Long id);
    List<Event> findByLocationName(String name);
    Optional<Event> findByLocationIdAndId(Long locationId, Long id);
//    List<Event> findByLocationNameAndBetween(String LocationName, Date start, Date end);

}
