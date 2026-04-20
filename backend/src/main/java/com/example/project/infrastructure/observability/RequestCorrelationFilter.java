package com.example.project.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestCorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        request.setAttribute(RequestCorrelationConstants.REQUEST_ID_ATTRIBUTE, requestId);
        response.setHeader(RequestCorrelationConstants.REQUEST_ID_HEADER, requestId);
        MDC.put(RequestCorrelationConstants.MDC_KEY, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(RequestCorrelationConstants.MDC_KEY);
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String header = request.getHeader(RequestCorrelationConstants.REQUEST_ID_HEADER);
        return header == null || header.isBlank() ? UUID.randomUUID().toString() : header;
    }
}
