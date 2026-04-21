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
import java.util.concurrent.locks.ReentrantLock;
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

    private final ReentrantLock lock = new ReentrantLock();

@Transactional
public BookingResponseDTO createBooking(String email, Long eventId, List<Long> seatIds) {
    lock.lock();
    try {
        System.out.println("Thread [" + Thread.currentThread().getName() + "] entered createBooking.");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + eventId + " not found."));

        if (new HashSet<>(seatIds).size() != seatIds.size()) {
            throw new RuntimeException("You cannot book the same seat twice in one order!");
        }


        for (Long seatId : seatIds) {
            boolean isTaken = ticketRepository.existsByEventIdAndSeatIdAndBooking_BookingStatusNot(
                    eventId, seatId, BookingStatus.CANCELED
            );
            if (isTaken) {
                throw new RuntimeException("Seat " + seatId + " is already booked for this event.");
            }
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
            ticket.setPrice(ticketPrice);
            ticket.setUniqueHash(UUID.randomUUID().toString());

            tickets.add(ticket);
            totalAmount += ticketPrice;
        }

        booking.setTickets(tickets);
        booking.setTotalAmount(totalAmount);

        try {
            Booking savedBooking = bookingRepository.save(booking);
            return convertToDTO(savedBooking);
        } catch (DataIntegrityViolationException e) {
            if (e.getRootCause().getMessage().contains("ukh31cugls")) {
                throw new RuntimeException("Race condition: One or more selected seats were just booked.");
            }
            throw new RuntimeException("Booking failed: " + e.getRootCause().getMessage());
        }

    } finally {
        lock.unlock();
        System.out.println("Thread [" + Thread.currentThread().getName() + "] released the lock.");
    }
}


    @Transactional
    public BookingResponseDTO confirmBooking(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID " + bookingId + " not found."));

        if (booking.getBookingStatus() != BookingStatus.PENDING){
            throw new RuntimeException("Cannot confirm booking in "+ booking.getBookingStatus()+ " status.");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);

        return convertToDTO(savedBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not authorized to cancel this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELED) {
            throw new RuntimeException("Booking is already canceled.");
        } else if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new RuntimeException("Your booking is confirmed, You Cannot cancel this booking.");
        }

        booking.setBookingStatus(BookingStatus.CANCELED);

        bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new EntityNotFoundException("Booking not found.");
        }
        bookingRepository.deleteById(bookingId);
    }



    private BookingResponseDTO convertToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getBookingStatus().toString());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUsername(booking.getUser().getUsername());

        List<TicketDTO> ticketDTOs = booking.getTickets().stream().map(ticket -> {
            TicketDTO ticketDTO = new TicketDTO();
            ticketDTO.setTicketId(ticket.getId());
            ticketDTO.setEventName(ticket.getEvent().getEventName());
            ticketDTO.setSeatNumber(ticket.getSeat().getSeatNumber());
            ticketDTO.setUniqueHash(ticket.getUniqueHash());
            ticketDTO.setPrice(ticket.getPrice());
            return ticketDTO;
        }).collect(Collectors.toList());

        dto.setTickets(ticketDTOs);
        return dto;
    }


}
