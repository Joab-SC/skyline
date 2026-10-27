package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.LegResponse;
import com.uniquindio.skyline.application.dto.response.LegSeatMapResponse;
import com.uniquindio.skyline.application.dto.response.LegSeatResponse;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.LegSeat;
import org.springframework.stereotype.Component;

@Component
public class LegMapper {

    public LegResponse toLegResponse(Leg leg) {
        return new LegResponse(
                leg.getId(),
                leg.getOriginAirport().name(),
                leg.getDestinationAirport().name(),
                leg.getDepartureTime(),
                leg.getArrivalTime(),
                leg.getAirlineId(),
                leg.getAircraftId(),
                leg.getLuggage().getPrice(),
                leg.getPrice(),
                leg.getAvailableSeats()
        );
    }

    public LegSeatMapResponse toLegSeatMapResponse(Leg leg) {
        return new LegSeatMapResponse(
                leg.getId(),
                leg.getSeats().stream().map(this::toLegSeatResponse).toList()
        );
    }

    private LegSeatResponse toLegSeatResponse(LegSeat legSeat) {
        return new LegSeatResponse(
                legSeat.getId(),
                legSeat.getSeatCode(),
                legSeat.getSeatStatus().name()
        );
    }
}