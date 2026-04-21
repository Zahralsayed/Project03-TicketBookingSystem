package com.ga.TicketSystem.controller;

import com.ga.TicketSystem.dto.BookingRequest;
import com.ga.TicketSystem.dto.BookingResponseDTO;
import com.ga.TicketSystem.model.Booking;
import com.ga.TicketSystem.model.User;
import com.ga.TicketSystem.repository.UserRepository;
import com.ga.TicketSystem.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, Principal principal) {
        try {
            BookingResponseDTO response = bookingService.createBooking(principal.getName(), request.getEventId(), request.getSeatIds());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponseDTO> confirmBooking(@PathVariable Long id) {
        BookingResponseDTO confirmed = bookingService.confirmBooking(id);
        return ResponseEntity.ok(confirmed);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, Principal principal) {
        bookingService.cancelBooking(id, principal.getName());
        return ResponseEntity.ok(Map.of("message", "Your booking has been canceled."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(Map.of("message", "Admin: Booking deleted permanently."));
    }




//    @GetMapping("/all")
//    public ResponseEntity<?> getAllBookings(Principal principal) {
//
//    }


}
