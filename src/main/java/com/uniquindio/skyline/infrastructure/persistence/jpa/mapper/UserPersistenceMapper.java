package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapper {

    private final PersonPersistenceMapper personMapper;

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPasswordHash());
        entity.setRole(user.getRole());
        entity.setAirlineId(user.getAirlineId().orElse(null));
        entity.setPerson(personMapper.toEmbeddable(user.getPerson()));
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return User.reconstruct(
                entity.getId(),
                personMapper.toDomain(entity.getPerson()),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getAirlineId());
    }
}
