package com.ga.TicketSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "locations")
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private int capacity;

    @OneToMany(mappedBy = "location")
    private List<Event> events;

    @OneToMany(mappedBy = "location")
    private List<Seat> seats;
}
