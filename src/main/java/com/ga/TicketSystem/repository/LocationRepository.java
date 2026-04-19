package com.ga.TicketSystem.repository;

import com.ga.TicketSystem.dto.LocationSummaryDTO;
import com.ga.TicketSystem.model.Event;
import com.ga.TicketSystem.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByNameAndAddress(String name, String address);
    Optional<Location> findByName(String name);
    List<LocationSummaryDTO> findAllBy();
}
