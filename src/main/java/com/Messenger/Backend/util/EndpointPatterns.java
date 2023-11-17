package com.Messenger.Backend.util;

public class EndpointPatterns {
    /**
     * An array of patterns representing endpoints that are Authorised.
     * Add the desired endpoints to this array.
     */
    public static final String[] AUTHORISED_ENDPOINTS = { "/api/**", "/api/getChatNames/*", "/api/getMessage/*",
            "/user/**",  "/user/**", "/app/**" };

    /**
     * An array of patterns representing endpoints that are allowed.
     * Add the desired endpoints to this array.
     */
    public static final String[] ALLOW_ALL_ENDPOINTS = { "/login", "/register", "/user/**", "/auth/authenticateUser", "/authLogout",
            "redirect:/login", "/inputRegister","/ws/**" };

}
