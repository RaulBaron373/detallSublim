package com.detallsublim.app.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ProductRequestSizeFilter extends OncePerRequestFilter {

    private static final long MAX_REQUEST_SIZE = 2L * 1024L * 1024L;

    private static final Set<String> LIMITED_METHODS = Set.of("POST", "PUT", "PATCH");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!LIMITED_METHODS.contains(request.getMethod())) {
            return true;
        }

        String path = request.getServletPath();

        return !("/api/productos".equals(path) || path.startsWith("/api/productos/"));
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
