package com.uniquindio.skyline.infrastructure.rest.mapper;

import com.uniquindio.skyline.application.dto.response.UserResponse;
import com.uniquindio.skyline.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // A CUSTOMER has no airline, so its airlineId is null.
    public UserResponse toRegisterResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getAirlineId().orElse(null)
        );
    }
}
