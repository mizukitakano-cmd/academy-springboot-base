package com.spring.springbootapplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<SignupForm, Long> {
    boolean existsByEmail(String email);


    /*java.util.Optional<SignupForm> findByEmail(String email);*/
}