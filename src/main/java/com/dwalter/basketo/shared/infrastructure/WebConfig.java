package com.dwalter.basketo.shared.infrastructure;

import com.dwalter.basketo.modules.identity.infrastructure.adapters.JwtUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtUtils jwtUtils) {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtUtils));
        registrationBean.addUrlPatterns("/api/orders/*"); // Apply only to orders
        return registrationBean;
    }
}
