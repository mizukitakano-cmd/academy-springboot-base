package com.spring.springbootapplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<SignupForm, Long> {
    java.util.Optional<SignupForm> findByEmail(String email);
}