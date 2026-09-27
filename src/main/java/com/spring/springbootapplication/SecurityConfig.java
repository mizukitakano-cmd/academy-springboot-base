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
            
        //パスワード自動暗号化処理
        try {
            org.springframework.jdbc.core.JdbcTemplate jdbcTemplate = new org.springframework.jdbc.core.JdbcTemplate(
                http.getSharedObject(javax.sql.DataSource.class)
            );
            // パスワードがまだ暗号化
            jdbcTemplate.query("SELECT email, password FROM users WHERE email = 'hokusai@fugaku.com'", (rs, rowNum) -> {
                String email = rs.getString("email");
                String rawPw = rs.getString("password");
                if (rawPw != null && !rawPw.startsWith("$2a$")) {
                    String encrypted = passwordEncoder().encode(rawPw);
                    jdbcTemplate.update("UPDATE users SET password = ? WHERE email = ?", encrypted, email);
                }
                return null;
            });
        } catch (Exception e) {

        }
            
        return http.build();
    }
}