package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.UserRepository;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAirlineAdminsUseCase {

    private final AirlineRepository airlineRepository;
    private final UserRepository userRepository;

    // Returns the admins of the airline, ordered by email. The list can be empty.
    @Transactional(readOnly = true)
    public List<User> execute(String airlineId) {
        airlineRepository.findById(airlineId).orElseThrow();   // 404 for an airline that does not exist
        return userRepository.findByAirlineAndRole(airlineId, UserRole.AIRLINE_ADMIN);
    }
}
