package com.uniquindio.skyline.domain.repository;

import com.uniquindio.skyline.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(String email);
    void save(User user);
}