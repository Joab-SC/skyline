package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.ExtraServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ExtraServicePersistenceMapper {

    public ExtraServiceEntity toEntity(ExtraService extraService) {
        ExtraServiceEntity extraServiceEntity = new ExtraServiceEntity();
        extraServiceEntity.setId(extraService.getId());
        extraServiceEntity.setNameService(extraService.getNameService());
        extraServiceEntity.setDescription(extraService.getDescription().orElse(null));
        extraServiceEntity.setNationalPrice(extraService.getNationalPrice());
        extraServiceEntity.setInternationalPrice(extraService.getInternationalPrice());
        extraServiceEntity.setDeleted(extraService.isDeleted());
        return extraServiceEntity;
    }

    public ExtraService toDomain(ExtraServiceEntity extraServiceEntity) {
        return ExtraService.reconstruct(extraServiceEntity.getId(), extraServiceEntity.getNameService(),
                extraServiceEntity.getDescription(),extraServiceEntity.getNationalPrice(),extraServiceEntity.getInternationalPrice(),extraServiceEntity.isDeleted());
    }
}
