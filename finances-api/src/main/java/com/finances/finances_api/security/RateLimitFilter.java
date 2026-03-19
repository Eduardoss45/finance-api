package com.finances.finances_api.security;

import java.io.IOException;
import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final Bucket loginBucket;
    private final Bucket refreshBucket;

    public RateLimitFilter() {
        loginBucket = Bucket.builder().addLimit(Bandwidth.simple(5, Duration.ofMinutes(1))).build();

        refreshBucket = Bucket.builder().addLimit(Bandwidth.simple(10, Duration.ofMinutes(1))).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String path = request.getRequestURI();

        if (path.equals("/auth/login")) {
            if (loginBucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                response.setStatus(429);
                response.getWriter().write("{\"message\":\"Too many login attempts\"}");
            }
            return;
        }

        if (path.equals("/auth/refresh")) {
            if (refreshBucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                response.setStatus(429);
                response.getWriter().write("{\"message\":\"Too many refresh attempts\"}");
            }
            return;
        }

        chain.doFilter(request, response);
    }
}
