package com.dwalter.basketo.shared.infrastructure;

import com.dwalter.basketo.modules.identity.infrastructure.adapters.JwtUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS configuration is now handled in SecurityConfig

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtUtils jwtUtils) {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtUtils));
        registrationBean.addUrlPatterns("/api/orders/*", "/api/admin/*");
        return registrationBean;
    }
}
