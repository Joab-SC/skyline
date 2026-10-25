package com.uniquindio.skyline.infrastructure.rest.mapper;


import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.domain.entity.ExtraService;
import org.springframework.stereotype.Component;

@Component
public class ExtraServiceMapper {

    public ExtraServiceResponse toExtraServiceResponse(ExtraService extraService) {
        return new ExtraServiceResponse(
                extraService.getId(),
                extraService.getNameService(),
                extraService.getDescription().orElse(null),
                extraService.getNationalPrice(),
                extraService.getInternationalPrice()
        );
    }
}
