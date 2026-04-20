package com.ga.TicketSystem.service;

import com.ga.TicketSystem.model.Event;
import com.ga.TicketSystem.model.Location;
import com.ga.TicketSystem.repository.EventRepository;
import com.ga.TicketSystem.repository.LocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private EventRepository eventRepository;
    private LocationRepository locationRepository;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Event creatEvent(Event event){
        if (!locationRepository.existsById(event.getLocation().getId())) {
            throw new RuntimeException("Location ID " + event.getLocation().getId() + " not found.");
        }

        List<Event> existingEvents = eventRepository.findByLocationId(event.getLocation().getId());
        for (Event existingEvent : existingEvents) {
            if (event.getStartTime().isBefore(existingEvent.getEndTime()) && event.getEndTime().isAfter(existingEvent.getStartTime())) {
                throw new RuntimeException("Schedule Conflict: The location is already booked for " +existingEvent.getEventName() + " during this Time. ");
            }
        }
        return eventRepository.save(event);
    }

    public List<Event> findAll(){
        return eventRepository.findAll();
    }

    public Optional<Event> findById(Long id){
        return eventRepository.findById(id);
    }

    public List<Event> findByLocationId(Long id){
        return eventRepository.findByLocationId(id);
    }

    public List<Event> findByLocationName(String LocationName){
        return eventRepository.findByLocationName(LocationName);
    }

    public Optional<Event> findByLocationIdAndEventId(Long id, Long eventId){
        return eventRepository.findByLocationIdAndId(id,eventId);
    }

    public String delete(Long id){
        eventRepository.deleteById(id);
        return "Admin deleted Event.";
    }

//    public List<Event> findByLocationNameAndBetween(String LocationName, Date start, Date end){
//        return eventRepository.findByLocationNameAndBetween(LocationName, start, end);
//    }
}
