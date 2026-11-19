package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.UserRepository;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAirlineSellersUseCase {

    private final UserRepository userRepository;

    // Returns the sellers of the airline, ordered by email. The list can be empty.
    @Transactional(readOnly = true)
    public List<User> execute(String airlineId) {
        return userRepository.findByAirlineAndRole(airlineId, UserRole.AIRLINE_SELLER);
    }
}
