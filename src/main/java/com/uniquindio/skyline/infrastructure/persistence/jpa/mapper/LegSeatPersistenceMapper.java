package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.LegSeat;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LegSeatEntity;
import org.springframework.stereotype.Component;

@Component
public class LegSeatPersistenceMapper {

    public LegSeatEntity toEntity(LegSeat legSeat) {
        LegSeatEntity legSeatEntity = new LegSeatEntity();
        legSeatEntity.setId(legSeat.getId());
        legSeatEntity.setSeatCode(legSeat.getSeatCode());
        legSeatEntity.setSeatStatus(legSeat.getSeatStatus());
        legSeatEntity.setPassengerId(legSeat.getPassengerId().orElse(null));
        return legSeatEntity;
    }

    public LegSeat toDomain(LegSeatEntity legSeatEntity) {
        return LegSeat.reconstruct(legSeatEntity.getId(), legSeatEntity.getSeatCode(),
                legSeatEntity.getSeatStatus(), legSeatEntity.getPassengerId());
    }
}
