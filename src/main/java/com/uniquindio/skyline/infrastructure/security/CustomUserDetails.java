package com.uniquindio.skyline.infrastructure.security;

import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Adapter: Spring Security only understands UserDetails, so it wraps our UserEntity.
// The other UserDetails methods (isEnabled, isAccountNonLocked, ...) default to true.
public record CustomUserDetails(UserEntity user) implements UserDetails {

    // The role becomes the authority: SecurityRole.ADMIN -> "ADMIN".
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // The stored hash. Spring compares it with the password sent in the login.
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
