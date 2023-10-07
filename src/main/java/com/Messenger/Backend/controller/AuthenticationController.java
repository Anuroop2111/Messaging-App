package com.Messenger.Backend.controller;

import com.Messenger.Backend.model.JwtTokenValidateResponse;
import com.Messenger.Backend.model.UserCredentials;
import com.Messenger.Backend.service.CustomUserDetailService;
import com.Messenger.Backend.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.Http2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    private final CustomUserDetailService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthenticationController(CustomUserDetailService userDetailsService, PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/authenticateUser")
    public ResponseEntity<JwtTokenValidateResponse> authenticate(@RequestBody UserCredentials userCredentials){
        // if username is empty
        if (userCredentials.getUsername()==null){
            log.error("Username is empty");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userCredentials.getUsername());
        if (userDetails!=null){
            if (passwordEncoder.matches(userCredentials.getPassword(),userDetails.getPassword())){
                log.info("User validated successfully = {}", userCredentials.getUsername());
                JwtTokenValidateResponse jwtResponse = jwtService.generateTokens(userCredentials.getUsername());
                log.info(jwtResponse.toString());
                return ResponseEntity.ok(jwtResponse);
            }
        }
//        return ResponseEntity.ok().build();
        log.error("UnAuthorised");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/get")
    public String getH(){
        return "Working";
    }

}
