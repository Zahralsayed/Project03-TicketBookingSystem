package com.ga.TicketSystem.controller;

import com.ga.TicketSystem.model.Event;
import com.ga.TicketSystem.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Event create(@RequestBody Event event){
        return eventService.creatEvent(event);
    }

    @GetMapping()
    public List<Event> getAll(){
        return  eventService.findAll();
    }

    @GetMapping("/location/{id}")
    public List<Event> getByLocationId(@PathVariable Long id){
        return eventService.findByLocationId(id);
    }


}
