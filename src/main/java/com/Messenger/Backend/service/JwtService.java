package com.Messenger.Backend.service;

import com.Messenger.Backend.entity.TokenData;
import com.Messenger.Backend.model.JwtInfoData;
import com.Messenger.Backend.model.JwtTokenValidateResponse;
import com.Messenger.Backend.model.JwtValidationData;
import com.Messenger.Backend.repo.TokenRepository;
import com.Messenger.Backend.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtService implements JwtServiceInterface{
    private final JwtUtil jwtUtil;

    private final TokenRepository tokenRepository;

    public JwtService(JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;

    }

    /**
     * Generates a JWT token, stores it in Redis, and returns the generated token.
     *
     * @param username  @return The generated JWT token.
     * @param refSeries
     */

    public String generateAndStoreJwt(String username, String refSeries) {
        String jwtToken = jwtUtil.generateToken(username, refSeries); //Generate a new jwt token.
        return jwtToken;
    }

    /**
     * Generates a refresh token, stores it in the database (Refresh_Token table), and returns the refSeries associated with the token.
     *
     * @param username  The username for which the refresh token is generated.
     * @param refSeries The refresh series associated with the token.
     * @return The refresh series associated with the token.
     */
    public String generateAndStoreRefreshToken(String username, String refSeries) { // Called once when the user is logged in successfully.
        String refreshToken = jwtUtil.generateRefreshToken(username); // Generate a new Refresh Token.
        TokenData existingTokenData = null; // Will be null during the initial login of the user.
        if (refSeries == null) {
            refSeries = jwtUtil.generateRefSeries(); // Generate a new refSeries.
        } else {
            existingTokenData = getByRefSeriesAndUsername(username, refSeries);
        }

        // Not needed in this situation. But, if using the UniqueIdentifier, this logic can be extended.
        if (existingTokenData != null) { // If the refresh Token had expired, replace the refresh token, creation date value. No need to change the refSeries and User-Agent (Because the Username and User-Agent should be same).
            existingTokenData.setRefreshToken(refreshToken);
            existingTokenData.setCreationDate(new Date());
            // If the token with a User-Agent + Username exists, we need to only update the refresh Token.(So that the old refSeries can be added to the jwt).
            tokenRepository.save(existingTokenData);

        } else { // If the username doesn't exist in refresh Token database, create a new Refresh Token with the corresponding refSeries, UserAgent, Username, etc.
            saveToken(username, refSeries, refreshToken);  // Storing Refresh Token in 'Refresh_Token' table in database.
        }
        return refSeries;
    }

    public ResponseEntity<JwtTokenValidateResponse> extractAndValidate(@RequestBody Map<String, Object> requestPayload) {
        String jwtToken = (String) requestPayload.get("jwtToken"); // Extract the JWT token from the request payload.
//        String uniqueIdentifier = (String) requestPayload.get("uniqueIdentifier"); // This can be passed on to the methods as required.
        String username;
        boolean isJwtExpiredFlag;
        String refSeries;

        if (jwtToken != null) {
            // Here, we are extracting the username from the jwt token. In case the jwt is expired, the jwtExpiredFlag = true
            JwtInfoData jwtInfo = ExtractJwtInfo(jwtToken);
            username = jwtInfo.getUsername();
            refSeries = jwtInfo.getRefSeries();
            isJwtExpiredFlag = jwtInfo.isJwtExpiredFlag();

            log.info("Username from jwt after checking for expiration. : " + username);
            if (username != null) {
                // 1. If JWT is expired. Then check the DB for Refresh Token.
                if (isJwtExpiredFlag) {
                    return refreshAndHandleExpiredJwt(refSeries, username, jwtToken);
                }

                // 2. If JWT is not expired.
                return handleNonExpiredJwt(refSeries, username, jwtToken);
            }
        }
        // If JWT Token is not found or if the username is null, the user should log in.
        return ResponseEntity.ok(createResponse(jwtToken, false, true, null));
    }

    /**
     * Validates the JWT token. If the token is not found in Redis, it checks for a valid refresh token
     * in the database and validates it.
     *
     * @param jwtToken The JWT token to validate.
     * @return A map containing the validation result and whether the JWT token is about to expire.
     * The map contains the keys "isValid" and "jwtAboutToExpire".
     */
    public JwtValidationData validateJwtToken(String jwtToken) {

        boolean isJwtAboutToExpire;
        try {
            isJwtAboutToExpire = jwtUtil.isTokenAboutToGetExpired(jwtToken);
        }catch(SignatureException e){
            return createJwtResponse(false,false);
        }
        String refSeries = jwtUtil.getRefSeriesFromToken(jwtToken);
        String username = jwtUtil.getUsernameFromToken(jwtToken);

        try {
            // Check if a valid refresh Token is available.
            return getRefreshTokenExpiryData(isJwtAboutToExpire, refSeries, username);

        } catch (Exception e) { // If not found in DB also return false.
            log.error(".User = {}. Some issue happened with checking for Refresh Token (when the jwt is not found in redis): {} ", username, e.getMessage());
            return createJwtResponse(false, isJwtAboutToExpire);
        }
    }


    /**
     * Validates the refresh token and checks if it is about to expire. If the token is about to expire,
     * a new refresh token is generated and stored.
     *
     * @param refreshToken The refresh token to validate.
     * @return {@code true} if the refresh token is valid, {@code false} otherwise.
     */
    public boolean validateRefreshToken(String refreshToken) {
        String refreshTokenUsername;

        try {
            refreshTokenUsername = jwtUtil.getUsernameFromToken(refreshToken);
        } catch (IllegalArgumentException e) {
            log.error("Unable to get Refresh Token -> IllegalArgumentException for refreshToken = {}", refreshToken);
            return false;
        } catch (ExpiredJwtException e) {
            log.warn("Refresh Token has expired : {}", refreshToken);
            return false;
        }
        return refreshTokenUsername != null;
    }

    public JwtTokenValidateResponse generateTokens(String username) {
//        String username = loginData.getUsername();
//        String uniqueIdentifier = loginData.getUniqueIdentifier();
        String refSeries = generateAndStoreRefreshToken(username, null); // Generate Refresh Token and add in DB. Returns RefSeries.
        String jwtToken = generateAndStoreJwt(username, refSeries); // Generate Jwt and add as cookie in response (Need to change to add in response, flag=true) + add it in redis

        // Create the response model with the JWT token and flags
        JwtTokenValidateResponse responseModel = JwtTokenValidateResponse.builder()
                .setFlag(true)
                .invalidFlag(false)
                .receivedJwtToken(jwtToken)
                .username(username)
                .build();

        log.info("JWT token and Refresh Token generated successfully. JWT Token set as cookie as well as stored in Redis and Refresh Token stored in Database. For user = {}", username);
        return responseModel;
    }

    public void deleteTokens(String jwtToken, String username) {
        log.info("Delete Refresh Token and Redis getting called. For user = {}", username);
        String refSeries;

        if (jwtToken != null) {
            try {
                refSeries = jwtUtil.getRefSeriesFromToken(jwtToken);
            } catch (ExpiredJwtException e) {
                refSeries = jwtUtil.getRefSeriesFromExpiredToken(jwtToken);
            }
            // Delete Refresh Token from DB.
            tokenRepository.deleteByRefSeriesAndUsername(refSeries, username);


        }
    }


    private TokenData getByRefSeriesAndUsername(String username, String refSeries) {
        return tokenRepository.findByRefSeriesAndUsername(refSeries, username);
    }

    private void saveToken(String username, String refSeries, String refreshToken) {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString().replace("-", "");

        TokenData tokenData = TokenData
                .builder()
                .id(id)
                .refSeries(refSeries)
                .username(username)
                .refreshToken(refreshToken)
                .creationDate(new Date())
                .revoked(false)
                .build();
        tokenRepository.save(tokenData);
    }


    private JwtValidationData getRefreshTokenExpiryData(boolean isJwtAboutToExpire, String refSeries, String username) {
        TokenData tokenData = getByRefSeriesAndUsername(username, refSeries);
        if (tokenData == null) {
            return createJwtResponse(false, isJwtAboutToExpire); // If the Refresh Token is not found.
        }

        String refreshToken = tokenData.getRefreshToken();
        if (refreshToken != null && validateRefreshToken(refreshToken)) {
            return createJwtResponse(true, isJwtAboutToExpire);  // If the Refresh Token is found and valid
        } else {
            log.warn("No Refresh Token found for this user = {} It is invalid.", username);
            return createJwtResponse(false, isJwtAboutToExpire); // If the Refresh Token is not found.
        }
    }

    private JwtValidationData createJwtResponse(boolean isValid, boolean isJwtAboutToExpire) {
        JwtValidationData result = new JwtValidationData();
        result.setValid(isValid);
        result.setJwtAboutToExpire(isJwtAboutToExpire);
        return result;
    }

    private JwtInfoData ExtractJwtInfo(String jwtToken) {
        // Here, we are extracting the username from the jwt token. In case the jwt is expired, the jwtExpiredFlag = true
        String username = null;
        String refSeries = null;
        boolean isJwtExpiredFlag = false;
        try {
            username = jwtUtil.getUsernameFromToken(jwtToken); // Throws ExpiredJwtException if jwt is expired.Else gets the name.
            refSeries = jwtUtil.getRefSeriesFromToken(jwtToken);

        } catch (IllegalArgumentException e) {
            log.error("Unable to get JWT Token -> IllegalArgumentException. For user = {}", username);
        } catch (SignatureException e) {
            log.warn("JWT has been tampered. For user = {}", username);
            // Logout the user.

        } catch (
                ExpiredJwtException e) { // If the JWT is expired we will decode and extract the username and check for Refresh Token. In this case, we don't check the redis.
            log.warn("JWT Token has expired for user = {}", username);
            isJwtExpiredFlag = true;
            username = jwtUtil.getUsernameFromExpiredToken(jwtToken);
            refSeries = jwtUtil.getRefSeriesFromExpiredToken(jwtToken);
        }

        // In case 'IllegalArgumentException' or 'SignatureException' or 'ExpiredJwtException' is called, the username will be null.

        // Create a Map to store the extracted information
        JwtInfoData jwtInfo = new JwtInfoData();
        jwtInfo.setUsername(username);
        jwtInfo.setRefSeries(refSeries);
        jwtInfo.setJwtExpiredFlag(isJwtExpiredFlag);

        return jwtInfo;
    }

    private ResponseEntity<JwtTokenValidateResponse> refreshAndHandleExpiredJwt(String refSeries, String username, String jwtToken) {
        //                    TokenData tokenData = tokenRepository.findByUsernameAndUniqueId(username,userAgent);
        TokenData tokenData = tokenRepository.findByRefSeriesAndUsername(refSeries, username);

        if (tokenData == null) {
            // Logout the user.
            return ResponseEntity.ok(createResponse(jwtToken, false, true, null));
        }

        // Getting the Refresh Token associated with the 'refSeries' and 'username'
        String refreshToken = tokenData.getRefreshToken();

        // Handling the expired jwt and regenerating a new jwt.
        jwtToken = HandleExpiredJwt(refreshToken, username, refSeries);

        if (jwtToken != null) {
            return ResponseEntity.ok(createResponse(jwtToken, true, false, username));
        } else {
            log.warn("Issue. Jwt and username are null.");
            // Some issue in regenerating a new jwt from the server side.
            return ResponseEntity.ok(createResponse(null, false, true, null));
        }
    }

    private String HandleExpiredJwt(String refreshToken, String username, String refSeries) {

        String jwtToken = null;
        // From jwt we will get refSeries. From refSeries we will get tokenData. Now, check if UserAgent and Username are equal from the Client Side and DB.
        if (refreshToken != null && validateRefreshToken(refreshToken)) { // If Refresh Token is not null, validate the Refresh Token -> Checking for expiry of Refresh Token.  && userAgent.equals(tokenData.getUniqueIdentifier())

            TokenData tokenData = tokenRepository.findByRefSeriesAndUsername(refSeries, username);

            // Create the new refSeries. And also set it in the token Repository.
            refSeries = jwtUtil.generateRefSeries();

            // After creating a new refSeries, replace the old refSeries in the database.
            tokenData.setRefSeries(refSeries);
            String uniqueIdentifier = tokenData.getUniqueIdentifier();
            tokenRepository.save(tokenData);

            // We have a valid Refresh Token. Create a new jwt, add it in response and update in redis.
            jwtToken = jwtUtil.generateTokenFromRefreshToken(refreshToken, refSeries, uniqueIdentifier); // The new jwt token is created using the existing Refresh Token (extracts the username from Refresh Token).

            refSeries = jwtUtil.getRefSeriesFromToken(jwtToken);

            log.info("JWT expired. Refresh Token is valid. Refreshed JWT Token + added in redis and response for user = {}", username);

        } else { // If the Refresh Token is null or Invalid (expired).
            tokenRepository.deleteByRefSeriesAndUsername(refSeries, username);
            log.warn("Refresh Token is Invalid or expired for user = {}", username);
        }

        return jwtToken;
    }

    private ResponseEntity<JwtTokenValidateResponse> handleNonExpiredJwt(String refSeries, String username, String jwtToken) {
        JwtValidationData validationMap = validateJwtToken(jwtToken); // Will get two flags. isValid and jwtAboutToExpire.

        boolean isValid = validationMap.isValid();
        boolean isJwtAboutToExpire = validationMap.isJwtAboutToExpire();

        if (isValid && !isJwtAboutToExpire) {
            log.info("JWT is valid, no need to update in the cookie and the user is also valid = {}", username);
            return ResponseEntity.ok(createResponse(jwtToken, false, false, username));

        } else if (isValid) {
            log.warn("Jwt valid, but about to expire for user = {}", username);
            // jwt is about to expire, follow the steps of the expired Jwt. This is called, if the jwt was not expired, but got expired when it reaches this step. Maybe Redundant, and can be removed.
            TokenData tokenData = tokenRepository.findByRefSeriesAndUsername(refSeries, username);
            if (tokenData == null) {
                // Logout the user.
                return ResponseEntity.ok(createResponse(jwtToken, false, true, null));
            }
            String refreshToken = tokenData.getRefreshToken();

            jwtToken = HandleExpiredJwt(refreshToken, username, refSeries);

            if (jwtToken != null) {
                // Sending new JWT
                return ResponseEntity.ok(createResponse(jwtToken, true, false, username));
            } else {
                return ResponseEntity.ok(createResponse(null, false, true, null));
            }
        }
        return ResponseEntity.ok(createResponse(jwtToken, false, true, null)); // If the jwt is not valid.
    }

    /**
     * Creates a response entity containing the JWT token and flags.
     *
     * @param receivedJwtToken The JWT token.
     * @param isSetFlag        The flag indicating if the token should be set.
     * @param isInvalidFlag    The flag indicating if the token is invalid.
     * @param username         The username associated with the token.
     * @return ResponseEntity containing the JWT token and flags.
     */
    private JwtTokenValidateResponse createResponse(String receivedJwtToken, boolean isSetFlag, boolean isInvalidFlag, String username) {
        return JwtTokenValidateResponse.builder()
                .receivedJwtToken(receivedJwtToken)
                .setFlag(isSetFlag)
                .invalidFlag(isInvalidFlag)
                .username(username)
                .build();
    }
}
