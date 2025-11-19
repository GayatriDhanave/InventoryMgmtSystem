package com.ims.inventoryManagementSystem.security;

import com.ims.inventoryManagementSystem.entity.UserData;
import com.ims.inventoryManagementSystem.repository.UserDataRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDataRepo repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserData user = repo.findByEmail(email);
        if (user == null){
            log.error(email + " not found");
            throw new UsernameNotFoundException("User not found with email: " + email);}
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}