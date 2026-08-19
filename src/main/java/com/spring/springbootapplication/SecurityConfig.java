package com.spring.springbootapplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception{
        http
            .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/login", "/signin", "/signup", "/register", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
        )

        .formLogin(login -> login
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .defaultSuccessUrl("/top", true)
            .failureUrl("/login?error")
            .permitAll()
        )

        .logout(logout -> logout
            .logoutSuccessUrl("/login?error")
            .permitAll()
        )

        .csrf(csrf -> csrf.disable());
            
    return http.build();

    }
}