package com.Messenger.Backend.controller;


import com.Messenger.Backend.model.JwtTokenValidateResponse;
//import com.Messenger.Backend.model.LoginData;
import com.Messenger.Backend.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/authenticate")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService){
        this.jwtService = jwtService;
    }

    /**
     * Validates the JWT token.
     *
     * @param requestPayload The request payload containing the JWT token.
     * @return ResponseEntity containing the validation response.
     * @throws Exception if an error occurs during token validation.
     */
    @PostMapping("/validateJwt")
    public ResponseEntity<JwtTokenValidateResponse> validateToken(@RequestBody Map<String, Object> requestPayload) throws Exception {
        log.info("Validate Called");
        return jwtService.extractAndValidate(requestPayload);
    }


    /**
     * Generates a new JWT token.
     *
     * @param loginData The request body containing the username.
     * @return ResponseEntity containing the generated JWT token.
     * @throws Exception if an error occurs during token generation.
     */
//    @PostMapping("/generateToken")
//    public ResponseEntity<JwtTokenValidateResponse> generateToken(@RequestBody LoginData loginData) throws Exception {
//        return jwtService.generateTokens(loginData);
//    }

    /**
     * Deletes the refresh token and associated session data from Redis.
     *
     * @param jwtToken  The JWT token. Used to get the refSeries.
     * @param username  The username associated with the token.
     * @param sessionId The session ID associated with the token.
     */
    @PostMapping("/refresh-session/delete")
    public void deleteRefreshTokenAndSession(String jwtToken, String username, String sessionId) {
        jwtService.deleteTokens(jwtToken, username);
        jwtService.deleteSession(sessionId, username);
    }

}
