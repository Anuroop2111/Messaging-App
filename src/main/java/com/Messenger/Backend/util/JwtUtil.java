package com.Messenger.Backend.util;

import com.Messenger.Backend.entity.UserData;
import com.Messenger.Backend.repo.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.RandomStringUtils;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
    private final String secretKey;
    private final int expirationTime;
    private final int refreshExpirationTime;
    private final int refreshJwtBeforeExpirationInMinutes;
    private final UserRepository userRepository;

    JwtUtil(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") int jwtExpirationTime, @Value("${jwt.refreshExpiration}") int refreshTokenExpirationTime, @Value("${jwt.refreshJwtBeforeExpirationInMinutes}") int refreshJwtBeforeExpirationInMinutes,UserRepository userRepository) {
        this.secretKey = jwtSecret;
        this.expirationTime = jwtExpirationTime;
        this.refreshExpirationTime = refreshTokenExpirationTime;
        this.refreshJwtBeforeExpirationInMinutes = refreshJwtBeforeExpirationInMinutes;
        this.userRepository = userRepository;
    }

    public static Date addMinutes(int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    public String generateToken(String username, String refSeries) {
        Map<String, Object> claims = new HashMap<>();

        // When we use user identifier or any Unique Id.
//        TokenData tokenData = tokenRepository.findByUsernameAndUniqueId(username,request.getHeader("User-Agent"));
//        String refSeries = tokenData.getRefSeries();

        // Claims can be added here.

        UserData userData = userRepository.findByEmail(username);

        claims.put("userid",userData.getId());
        claims.put("username", username);
        claims.put("refSeries", refSeries);

        return doGenerateToken(claims);
    }

    /**
     * Generate a JWT token from a refresh token.
     *
     * @param refreshToken The refresh token.
     * @param refSeries    The refresh series associated with the token.
     * @return The generated JWT token.
     */
    public String generateTokenFromRefreshToken(String refreshToken, String refSeries, String uniqueIdentifier) {
        String refreshUsername = getUsernameFromToken(refreshToken);

//        TokenData tokenData = tokenRepository.findByUsernameAndUniqueIdentifier(refreshUsername,uniqueIdentifier);
//        String refSeries = tokenData.getRefSeries();
        Map<String, Object> claims = new HashMap<>();
        // Claims can be added here.
        claims.put("username", refreshUsername); // here, instead of just username, we can also add different claims.
        claims.put("refSeries", refSeries);

        return doGenerateToken(claims);
    }

    private String doGenerateToken(Map<String, Object> claims) { //, String subject

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000L))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Generate a new refresh token for a user.
     *
     * @param username The username for the user.
     * @return The generated refresh token.
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationTime * 1000L);

        Map<String, Object> claims = new HashMap<>();
        // claims can be added here
        claims.put("username", username);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Retrieve the username from a JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("username", String.class));
    }

    /**
     * Retrieve the refresh series from a JWT token.
     *
     * @param token The JWT token.
     * @return The refresh series extracted from the token.
     */
    public String getRefSeriesFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("refSeries", String.class));
    }

    public String getUserIdFromToken(String token){
        return getClaimFromToken(token,claims -> claims.get("userid",String.class));
    }

    /**
     * Retrieve the refresh series from an expired JWT token.
     *
     * @param token The expired JWT token.
     * @return The refresh series extracted from the token.
     */
    public String getRefSeriesFromExpiredToken(String token) {
        try {
            return getClaimFromToken(token, claims -> claims.get("refSeries", String.class));
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("refSeries", String.class);
        } catch (Exception ex) {
            // Other exceptions, token is invalid or cannot extract username
            return null; // Return null or any default value as needed
        }
    }

    /**
     * Retrieve a claim from a JWT token.
     *
     * @param token          The JWT token.
     * @param claimsResolver The function to resolve the desired claim.
     * @param <T>            The type of the claim value.
     * @return The value of the claim.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieve all claims from a JWT token.
     *
     * @param token The JWT token.
     * @return The claims extracted from the token.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieve the username from an expired JWT token.
     *
     * @param token The expired JWT token.
     * @return The username extracted from the token.
     */
    public String getUsernameFromExpiredToken(String token) {
        try {
            return getClaimFromToken(token, claims -> claims.get("username", String.class));
        } catch (ExpiredJwtException e) {
            return e.getClaims().get("username", String.class);
        } catch (Exception ex) {
            // Other exceptions, token is invalid or cannot extract username
            return null; // Return null or any default value as needed
        }
    }

    /**
     * Retrieve the expiration date from a JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date extracted from the token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Check if a JWT token is about to expire.
     *
     * @param token The JWT token.
     * @return {@code true} if the token is about to expire, {@code false} otherwise.
     */
    public boolean isTokenAboutToGetExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        Date currentPlusRefresh = addMinutes(refreshJwtBeforeExpirationInMinutes); // This will add the specified number of minutes from the current date.

        return expiration.before(currentPlusRefresh); // Checking if the jwt token is gonna expire in 'refreshJwtBeforeExpirationInMinutes' minutes. If yes -> true.
    }

    /**
     * Generate a new refresh series.
     *
     * @return The generated refSeries.
     */
    public String generateRefSeries() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

}
