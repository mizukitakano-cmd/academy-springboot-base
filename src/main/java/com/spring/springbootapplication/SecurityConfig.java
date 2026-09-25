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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        　　//アクセス権限設定
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/signin", "/signup", "/register", "/top", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )

            //ログイン設定
                .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/top", true)
                .permitAll()
            )
            // ログアウトの設定
            .logout(logout -> logout
                .logoutSuccessUrl("/top")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }
}