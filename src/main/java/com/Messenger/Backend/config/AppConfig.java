package com.Messenger.Backend.config;

import com.Messenger.Backend.filter.JwtTokenFilter;
import com.Messenger.Backend.service.JwtService;
import com.Messenger.Backend.util.EndpointPatterns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.CorsConfiguration;

@EnableWebSecurity
@Configuration
public class AppConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
                // No encoding, just return the raw password as is
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                // Compare the encoded password with the raw password using HashUtils.getMD5HashForString()
                return rawPassword.equals(encodedPassword);
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/**", "/api/getChatNames/**", "/api/getMessage/**","/authenticate/**","/user/**","/auth/authenticateUser","/auth/**","/ws/**","/user/**","/app/**").permitAll()
                .anyRequest().authenticated()
                .and().cors().configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.addAllowedOrigin("http://localhost:3000");
                    corsConfig.addAllowedMethod("*");
                    corsConfig.addAllowedHeader("*");
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }).and().csrf().disable();
    }


//    @Bean
//    public FilterRegistrationBean<JwtTokenFilter> jwtTokenFilter() {
//        FilterRegistrationBean<JwtTokenFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new JwtTokenFilter());
//        for(String endpointPattern: EndpointPatterns.AUTHORISED_ENDPOINTS){
//            registrationBean.addUrlPatterns(endpointPattern);
//        }
//        return registrationBean;
//    }

}
    // Can be replaced by something simpler


//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/api/**").allowedOrigins("http://localhost:3000");
//            }
//        };
//    }


