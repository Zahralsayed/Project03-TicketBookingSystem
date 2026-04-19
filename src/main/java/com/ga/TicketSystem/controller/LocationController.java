package com.ga.TicketSystem.controller;

import com.ga.TicketSystem.dto.LocationRequest;
import com.ga.TicketSystem.dto.LocationSummaryDTO;
import com.ga.TicketSystem.model.Location;
import com.ga.TicketSystem.repository.LocationRepository;
import com.ga.TicketSystem.repository.SeatRepository;
import com.ga.TicketSystem.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    LocationService locationService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addLocation(@RequestBody LocationRequest location) {
        try {
            Location newLocation = locationService.createLocation(location);
            return new ResponseEntity<>(newLocation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public List<LocationSummaryDTO> getAll() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getLocation(@PathVariable("name") String name) {
        try {
            Location location = locationService.getLocationByName(name);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping({"/id/{id}"})
    public ResponseEntity<?> getLocationById(@PathVariable("id") long id) {
        try {
            Location location = locationService.getById(id);
                    return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLocation(@PathVariable("id") long id) {
        locationService.delete(id);
    }


}

