package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.Luggage;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.LuggageEntity;
import org.springframework.stereotype.Component;

@Component
public class LuggagePersistenceMapper {

    // The leg reference is set by LegPersistenceMapper, which owns the relation.
    public LuggageEntity toEntity(Luggage luggage) {
        LuggageEntity luggageEntity = new LuggageEntity();
        luggageEntity.setId(luggage.getId());
        luggageEntity.setPrice(luggage.getPrice());
        return luggageEntity;
    }

    public Luggage toDomain(LuggageEntity luggageEntity) {
        return Luggage.reconstruct(luggageEntity.getId(), luggageEntity.getPrice());
    }
}
