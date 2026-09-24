package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import org.junit.jupiter.api.Test;

import java.security.Security;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LegSeatTest {

    @Test
    public void LegSeatIsCreatedAvailable() {
        // Checks the initial seat status.
        LegSeat legSeat = LegSeat.createLegSeat("A32");
        assertEquals(SeatStatus.AVALIABLE, legSeat.getSeatStatus());
    }

    @Test
    public void LegSeatIsCreatedWthEmptyPassengerId() {
        // Checks that no passenger is assigned.
        LegSeat legSeat = LegSeat.createLegSeat("B28");
        assertEquals(Optional.empty(), legSeat.getPassengerId());
    }


}
