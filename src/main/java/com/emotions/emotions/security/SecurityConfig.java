package com.emotions.emotions.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.emotions.emotions.impls.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;

    SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // public SpringSecurityDialect springSecurityDialect() {
    //     return new SpringSecurityDialect();
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable).formLogin(httForm -> {
            httForm.loginPage("/login").usernameParameter("email").permitAll();
        })
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/css/**", "/js/**", "/images/**", "/survey/**").permitAll();
                    registry.requestMatchers("/register/**", "/login").anonymous();
                    registry.anyRequest().authenticated();
                })
                .build();
    }
}
