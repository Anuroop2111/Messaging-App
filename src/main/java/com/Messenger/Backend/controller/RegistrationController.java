package com.Messenger.Backend.controller;

import com.Messenger.Backend.entity.UserData;
import com.Messenger.Backend.service.CustomUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class RegistrationController {
    private final CustomUserService customUserService;

    public RegistrationController(CustomUserService customUserService) {
        this.customUserService = customUserService;
    }

    @PostMapping("/register")
    public UserData register(@RequestBody UserData userData) {
        return customUserService.register(userData);
    }


}

