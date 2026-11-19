package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.valueObject.UserRole;

import java.util.List;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(String email);
    void save(User user);
    // The users of an airline with that role, ordered by email.
    List<User> findByAirlineAndRole(String airlineId, UserRole role);
}