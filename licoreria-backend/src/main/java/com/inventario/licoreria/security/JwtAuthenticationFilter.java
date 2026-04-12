package com.inventario.licoreria.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader(AUTH_HEADER);
        System.out.println("[JWT FILTER] Path: " + request.getRequestURI() + " | authHeader: " + (authHeader != null ? "SET" : "NULL"));
        
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String jwt = authHeader.substring(BEARER_PREFIX.length());
            String username = jwtUtil.extractUsername(jwt);
            boolean isValid = jwtUtil.isTokenValid(jwt);
            System.out.println("[JWT FILTER] JWT parsed | username=" + username + " | valid=" + isValid);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (isValid) {
                    String role = jwtUtil.extractRole(jwt);
                    System.out.println("[JWT FILTER] Setting auth | user=" + username + " | role=" + role);
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JWT FILTER] Auth SET successfully");
                } else {
                    System.out.println("[JWT FILTER] Token INVALID");
                }
            } else {
                System.out.println("[JWT FILTER] username=" + username + " | existing auth=" + (SecurityContextHolder.getContext().getAuthentication() != null));
            }
        } else {
            System.out.println("[JWT FILTER] No Bearer token found");
        }
        filterChain.doFilter(request, response);
    }
}