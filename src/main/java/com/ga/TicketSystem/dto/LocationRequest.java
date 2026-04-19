package com.ga.TicketSystem.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String name;
    private String address;
    private int capacity;
    private int vipCount;
    private int goldCount;
    private int silverCount;
}