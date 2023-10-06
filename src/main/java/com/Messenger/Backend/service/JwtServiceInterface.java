package com.Messenger.Backend.service;

import com.Messenger.Backend.model.JwtTokenValidateResponse;
import com.Messenger.Backend.model.JwtValidationData;
import com.Messenger.Backend.model.LoginData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface JwtServiceInterface {

    String generateAndStoreJwt(LoginData loginData, String refSeries);

    String generateAndStoreRefreshToken(String username, String uniqueIdentifier, String refSeries);

    ResponseEntity<JwtTokenValidateResponse> extractAndValidate(@RequestBody Map<String, Object> requestPayload);

    JwtValidationData validateJwtToken(String jwtToken);

    boolean validateRefreshToken(String refreshToken);

    ResponseEntity<JwtTokenValidateResponse> generateTokens(LoginData loginData);

    void deleteTokens(String jwtToken, String username);

    void deleteSession(String sessionId, String username);
}
