package com.detallsublim.app.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicRequestSizeFilter extends OncePerRequestFilter {

    /*
     * Ninguno de estos formularios debería
     * acercarse remotamente a 64 KB.
     */
    private static final long MAX_REQUEST_SIZE = 64L * 1024L;

    private static final Set<String> LIMITED_ENDPOINTS = Set.of(
        "/api/authenticate",
        "/api/account/reset-password/init",
        "/api/account/reset-password/finish",
        "/api/public/contact",
        "/api/public/quote-request"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return true;
        }

        return !LIMITED_ENDPOINTS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        long contentLength = request.getContentLengthLong();

        if (contentLength > MAX_REQUEST_SIZE) {
            response.sendError(HttpStatus.PAYLOAD_TOO_LARGE.value(), "Request body too large");

            return;
        }

        filterChain.doFilter(request, response);
    }
}
