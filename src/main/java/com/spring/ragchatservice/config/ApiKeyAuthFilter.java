package com.spring.ragchatservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ragchatservice.dto.APIResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${app.api.key}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip API key validation for public endpoints
        if (requestPath.startsWith("/actuator/health")
                || requestPath.startsWith("/swagger-ui")
                || requestPath.startsWith("/v3/api-docs")
                || requestPath.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validate API key for protected API endpoints
        if (requestPath.startsWith("/api/")) {
            String apiKey = request.getHeader("X-API-Key");

            if (apiKey == null || !apiKey.equals(validApiKey)) {
                log.warn("Invalid API key attempt from IP: {}", request.getRemoteAddr());
                SecurityContextHolder.clearContext();

                APIResponse<Object> responseBody = new APIResponse<>(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid API Key",
                        null
                );

                // Set response headers and write JSON
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
                return;
            }

            // Set authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken("api-user", null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}