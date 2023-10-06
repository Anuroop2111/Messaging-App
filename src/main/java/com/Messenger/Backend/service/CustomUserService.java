package com.Messenger.Backend.service;
import com.Messenger.Backend.entity.UserData;
import com.Messenger.Backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService {
    @Autowired
    private UserRepository userRepository;

    public UserData register(UserData userData) {
        UserData userEntity = UserData.builder()
                .id(userData.getId())
                .username(userData.getUsername())
                .name(userData.getName())
                .email(userData.getEmail())
                .phone(userData.getPhone())
                .password(userData.getPassword()) // Password is already encoded from the product service and sent here.
                .build();

        return userRepository.save(userEntity);
    }
}
