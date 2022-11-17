package com.antifraud_System.service;


import com.antifraud_System.entity.UserDetailsImp;
import com.antifraud_System.entity.UserEntity;
import com.antifraud_System.exception.UserNotFoundException;
import com.antifraud_System.reposiroty.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    UserRepository userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepo.findByUsernameIgnoreCase(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Not found: " + username);
        }
        return new UserDetailsImp(user.get());
    }
}
