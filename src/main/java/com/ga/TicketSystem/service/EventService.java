package com.ga.TicketSystem.service;

import com.ga.TicketSystem.model.Event;
import com.ga.TicketSystem.model.Location;
import com.ga.TicketSystem.repository.EventRepository;
import com.ga.TicketSystem.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private EventRepository eventRepository;
    private LocationRepository locationRepository;

    public Event creatEvent(Event event){
        Location location = locationRepository.findById(event.getLocation().getId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        Event e = new Event();
        e.setEventName(event.getEventName());
        e.setDescription(event.getDescription());
        e.setStartTime(event.getStartTime());
        e.setLocation(location);

        return eventRepository.save(e);
    }

    public List<Event> findAll(){
        return eventRepository.findAll();
    }

    public Optional<Event> findById(Long id){
        return eventRepository.findById(id);
    }

    public List<Event> findByLocationId(Long locationId){
        return eventRepository.findByLocationId(locationId);
    }
}
