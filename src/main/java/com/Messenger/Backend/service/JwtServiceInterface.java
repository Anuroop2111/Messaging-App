package com.Messenger.Backend.service;

import com.Messenger.Backend.model.JwtTokenValidateResponse;
import com.Messenger.Backend.model.JwtValidationData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface JwtServiceInterface {



    String generateAndStoreRefreshToken(String username, String refSeries);

    ResponseEntity<JwtTokenValidateResponse> extractAndValidate(@RequestBody Map<String, Object> requestPayload);

    JwtValidationData validateJwtToken(String jwtToken);

    boolean validateRefreshToken(String refreshToken);

    JwtTokenValidateResponse generateTokens(String username);

    void deleteTokens(String jwtToken, String username);


}
