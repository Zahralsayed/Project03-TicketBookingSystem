package com.ga.TicketSystem.repository;

import com.ga.TicketSystem.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
