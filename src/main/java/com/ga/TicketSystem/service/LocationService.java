package com.ga.TicketSystem.service;

import com.ga.TicketSystem.dto.LocationRequest;
import com.ga.TicketSystem.dto.LocationSummaryDTO;
import com.ga.TicketSystem.enums.SeatCategory;
import com.ga.TicketSystem.enums.SeatStatus;
import com.ga.TicketSystem.model.Event;
import com.ga.TicketSystem.model.Location;
import com.ga.TicketSystem.model.Seat;
import com.ga.TicketSystem.repository.LocationRepository;
import com.ga.TicketSystem.repository.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SeatRepository seatRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<LocationSummaryDTO> getAllLocations() {
        return locationRepository.findAllBy();
    }

    public Location getLocationByName(String locationName) {
        return locationRepository.findByName(locationName)
                .orElseThrow(()-> new RuntimeException("Location not found"));
    }

    @Transactional
    public Location createLocation(LocationRequest request) {
        if (locationRepository.existsByNameAndAddress(request.getName(), request.getAddress())) {
            throw new RuntimeException("A Location with the same name and address already exists");
        }

        if (request.getCapacity() <= 0){
            throw new RuntimeException("Capacity must be greater than zero");
        }

        Location location = new Location();
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setCapacity(request.getCapacity());
        Location savedlocation = locationRepository.save(location);

        List<Seat> seats = new ArrayList<>();
        int seatsPerRow = 10;

        for (int i = 0; i < request.getCapacity(); i++) {
            Seat seat = new Seat();

            // Category Logic based on the counts provided in JSON
            if (i < request.getVipCount()) {
                seat.setCategory(SeatCategory.VIP);
            } else if (i < (request.getVipCount() + request.getGoldCount())) {
                seat.setCategory(SeatCategory.GOLD);
            } else if (i < (request.getVipCount() + request.getGoldCount() + request.getSilverCount())) {
                seat.setCategory(SeatCategory.SILVER);
            } else {
                seat.setCategory(SeatCategory.STANDARD);
            }

            // Set row and seat numbers
            seat.setRowNumber(String.valueOf((char) ('A' + (i / seatsPerRow))));
            seat.setSeatNumber(String.valueOf((i % seatsPerRow) + 1));
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLocation(savedlocation);
            seats.add(seat);
        }

        seatRepository.saveAll(seats);
        savedlocation.setSeats(seats);

        return savedlocation;
    }

    public Location getById(long id) {
        return locationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Location not found"));
    }

    public void delete(Long id) {
        Location location = locationRepository.getById(id);
        locationRepository.delete(location);
        System.out.println("Admin deleted " + location.getName() + " location.");
    }


}
