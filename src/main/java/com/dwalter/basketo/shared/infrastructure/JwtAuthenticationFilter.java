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
        
        // Public endpoints: /auth/*, /carts/*, /payments/*, /api/admin/login
        if (!path.startsWith("/api/orders") && !path.startsWith("/api/admin")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (path.equals("/api/admin/login")) {
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
        String role = jwtUtils.getRole(token);

        if (email == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid Token");
            return;
        }

        request.setAttribute("userEmail", email);
        request.setAttribute("userRole", role);
        filterChain.doFilter(request, response);
    }
}
