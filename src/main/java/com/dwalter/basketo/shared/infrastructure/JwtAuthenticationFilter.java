package com.dwalter.basketo.shared.infrastructure;

import com.dwalter.basketo.modules.identity.infrastructure.adapters.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // If no Authorization header, continue (Spring Security will handle it)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtUtils.validateAndGetEmail(token);
            String role = jwtUtils.getRole(token);

            if (email != null) {
                // Create Spring Security Authentication
                var authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(role != null ? role : "ROLE_USER")
                );
                var authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    authorities
                );

                // Set authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Also set as request attributes for backward compatibility
                request.setAttribute("userEmail", email);
                request.setAttribute("userRole", role);
            }
        } catch (Exception e) {
            // If token is invalid, let Spring Security handle it
            logger.warn("JWT validation failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
