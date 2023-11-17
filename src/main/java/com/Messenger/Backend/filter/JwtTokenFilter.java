package com.Messenger.Backend.filter;

import com.Messenger.Backend.model.JwtValidationData;
import com.Messenger.Backend.service.JwtService;
import com.Messenger.Backend.util.EndpointPatterns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Somehow, the OncePerRequestFilter is getting called even after .permitAll() on certain endpoints.
        //To skip OncePerRequestFilter for login and logout endpoint and other ALLOW_ALL_ENDPOINTS
        if (Arrays.asList(EndpointPatterns.ALLOW_ALL_ENDPOINTS).contains(request.getServletPath())) {
            log.info("Ignored Endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("The url inside OPRF = {}",request.getServletPath());

        Cookie[] cookies = request.getCookies();
        String jwtToken = null;
        String receivedJwtToken;
        boolean isSetFlag;
        boolean isInvalidFlag;
        String username;
        // Extract the jwt from the cookie from the request.
        log.info(String.valueOf(cookies[1].getName().equals("jwt-token")));
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info(cookie.getName());
                if (cookie.getName().equals("jwt-token")) {
                    log.info("set jwt");
                    jwtToken = cookie.getValue();
                    log.info(jwtToken);
                    break;
                }
            }
        }
        log.info("jwt Token : ",jwtToken);
        if (jwtToken != null) {
            // Here, validate the JwtToken.
            JwtValidationData validationResponse = jwtService.validateJwtToken(jwtToken);
            if(!validationResponse.isValid()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized - JWT token invalid");
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
        }else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - JWT token not found");
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }
        // Continue with the next filters.
        filterChain.doFilter(request, response);
    }
}

