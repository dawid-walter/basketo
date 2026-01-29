package com.dwalter.basketo.shared.infrastructure;

import com.dwalter.basketo.modules.identity.infrastructure.adapters.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        // Only protect specific endpoints for now (e.g., getting orders)
        // Public endpoints: /auth/*, /carts/*, /payments/* (webhooks)
        if (!path.startsWith("/api/orders")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtUtils.validateAndGetEmail(token);

        if (email == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid Token");
            return;
        }

        // Pass the email to the controller via request attribute
        request.setAttribute("userEmail", email);
        filterChain.doFilter(request, response);
    }
}
