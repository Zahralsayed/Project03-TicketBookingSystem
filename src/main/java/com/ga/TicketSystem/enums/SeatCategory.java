package com.ga.TicketSystem.enums;

import lombok.Getter;

@Getter
public enum SeatCategory {
    VIP(2.0),
    GOLD(1.5),
    SILVER(1.2),
    STANDARD(1.0);

    private final double multiplier;

    SeatCategory(double multiplier) {
        this.multiplier = multiplier;
    }

}
