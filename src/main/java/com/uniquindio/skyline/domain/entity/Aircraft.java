package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Seat;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Aircraft {
    private String id;
    private AircraftModel aircraftModel;
    private List<Seat> seats;

    private Aircraft(String id, AircraftModel aircraftModel, List<Seat> seats) {
        this.id = id;
        this.aircraftModel = aircraftModel;
        this.seats = seats;
    }

    public static Aircraft createAircraft(AircraftModel aircraftModel) {
        validateNotNullAircraftModel(aircraftModel);
        List<Seat> seats = generateSeats(aircraftModel);
        return new Aircraft(UUID.randomUUID().toString(), aircraftModel, seats);
    }

    private static void validateNotNullAircraftModel(AircraftModel aircraftModel) {
        if (aircraftModel == null){
            throw new DomainRuleException("The aircraft model can not be null");
        }
    }

    private static List<Seat> generateSeats(AircraftModel aircraftModel) {
        List<Seat> seats = new ArrayList<>();

        int rows = (int) Math.ceil(
                (double) aircraftModel.getMaxSeats()
                        / aircraftModel.getSeatColumns().size()
        );

        for (int row = 1; row <= rows; row++) {
            for (String column : aircraftModel.getSeatColumns()) {

                if (seats.size() >= aircraftModel.getMaxSeats()) {
                    break;
                }

                String seatCode = row + column;
                seats.add(new Seat(seatCode));
            }
        }

        return seats;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aircraft)) return false;
        Aircraft other = (Aircraft) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
