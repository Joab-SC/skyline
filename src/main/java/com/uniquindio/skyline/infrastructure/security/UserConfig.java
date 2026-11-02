package com.uniquindio.skyline.infrastructure.security;

import com.uniquindio.skyline.infrastructure.persistence.jpa.UserJpaDataRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserConfig {

    // How Spring finds a user by its username (the email).
    // With this bean and the PasswordEncoder bean, Spring builds the DaoAuthenticationProvider on its own:
    // it is the one that checks email + password in the login.
    @Bean
    public UserDetailsService userDetailsService(UserJpaDataRepository repository) {
        return email -> repository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
