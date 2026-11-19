package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.UserRepository;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.UserPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJpa implements UserRepository {

    private final UserJpaDataRepository dataRepository;
    private final UserPersistenceMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return dataRepository.existsByEmail(email);
    }

    @Override
    public void save(User user) {
        dataRepository.save(mapper.toEntity(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByAirlineAndRole(String airlineId, UserRole role) {
        return dataRepository.findByAirlineIdAndRoleOrderByEmailAsc(airlineId, role).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
