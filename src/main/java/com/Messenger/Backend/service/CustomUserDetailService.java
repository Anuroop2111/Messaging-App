package com.Messenger.Backend.service;

import com.Messenger.Backend.entity.UserData;
import com.Messenger.Backend.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserData customer = userRepository.findByEmail(username);
        if (Objects.isNull(customer)) {
            log.warn("Username not found for user : {}", username);
            throw new UsernameNotFoundException(username);
        }
        return mapToUserDetails(customer);

    }

    private UserDetails mapToUserDetails(UserData user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // Empty authorities or pass null if not needed
        );
    }
}