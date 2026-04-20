package com.ga.TicketSystem.service;

import com.ga.TicketSystem.dto.BookingResponseDTO;
import com.ga.TicketSystem.dto.TicketDTO;
import com.ga.TicketSystem.enums.BookingStatus;
import com.ga.TicketSystem.model.*;
import com.ga.TicketSystem.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketService ticketService;


@Transactional
public BookingResponseDTO createBooking(String email, Long eventId, List<Long> seatIds) {
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Event with ID " + eventId + " not found."));

    if (new HashSet<>(seatIds).size() != seatIds.size()) {
        throw new RuntimeException("You cannot book the same seat twice in one order!");
    }

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Booking booking = new Booking();
    booking.setUser(user);
    booking.setBookingStatus(BookingStatus.PENDING);

    double totalAmount = 0;
    List<Ticket> tickets = new ArrayList<>();

    for (Long seatId : seatIds) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat " + seatId + " not found"));

        Double ticketPrice = ticketService.calculateTicketPrice(event, seat);
        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setEvent(event);
        ticket.setSeat(seat);
        ticket.setUniqueHash(UUID.randomUUID().toString());

        tickets.add(ticket);
        ticket.setPrice(ticketPrice);
        totalAmount += ticketPrice;
    }

    booking.setTickets(tickets);
    booking.setTotalAmount(totalAmount);

    try {

        Booking savedBooking = bookingRepository.save(booking);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(savedBooking.getId());
        response.setTotalAmount(savedBooking.getTotalAmount());
        response.setStatus(savedBooking.getBookingStatus().toString());
        response.setCreatedAt(savedBooking.getCreatedAt());

        response.setUsername(savedBooking.getUser().getUsername());

        List<TicketDTO> ticketDTOs = savedBooking.getTickets().stream().map(t -> {
            TicketDTO dto = new TicketDTO();
            dto.setTicketId(t.getId());
            dto.setEventName(t.getEvent().getEventName());
            dto.setSeatNumber(t.getSeat().getSeatNumber());
            dto.setUniqueHash(t.getUniqueHash());
            dto.setPrice(t.getPrice());
            return dto;
        }).collect(Collectors.toList());

        response.setTickets(ticketDTOs);
        return response;
    } catch (DataIntegrityViolationException e) {
        e.printStackTrace();

        if (e.getRootCause().getMessage().contains("ukh31cugls")) {
            throw new RuntimeException("One or more selected seats are already booked for this event.");
        }

        throw new RuntimeException("Booking failed: " + e.getRootCause().getMessage());
    }
}

    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not authorized to cancel this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already canceled.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);
    }


}
