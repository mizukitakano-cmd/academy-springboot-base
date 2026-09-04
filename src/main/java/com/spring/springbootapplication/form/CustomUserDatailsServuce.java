package com.spring.springbootapplication.form;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spring.springbootapplication.UserRepository;


@Service
public class CustomUserDatailsServuce implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public  UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.spring.springbootapplication.User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + email));

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    
}
