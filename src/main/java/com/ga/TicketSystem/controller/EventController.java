package com.ga.TicketSystem.controller;

import com.ga.TicketSystem.model.Event;
import com.ga.TicketSystem.model.Location;
import com.ga.TicketSystem.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    private EventService eventService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/location/{locationId}/events/create")
    public ResponseEntity<?> create(@PathVariable("locationId") Long locationId, @RequestBody Event event){
        try {
            Location loc = new Location();
            loc.setId(locationId);
            event.setLocation(loc);

            Event newEvent = eventService.creatEvent(event);
            return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/events")
    public List<Event> getAll(){
        return  eventService.findAll();
    }

    @GetMapping("/location/{id}/events")
    public List<Event> getByLocationId(@PathVariable Long id){
        return eventService.findByLocationId(id);
    }

    @GetMapping("/location/{locationId}/event/{eventId}")
    public Optional<Event> getEventByLocationIdAndEventId(@PathVariable Long locationId, @PathVariable Long eventId){
        return eventService.findByLocationIdAndEventId(locationId, eventId);
    }

    @DeleteMapping("/event/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteById(@PathVariable Long id){
       return eventService.delete(id);
    }



}
