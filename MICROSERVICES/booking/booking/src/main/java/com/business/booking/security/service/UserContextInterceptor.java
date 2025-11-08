package com.business.booking.security.service;

import com.business.booking.util.dataclasses.UserContext;
import com.business.booking.util.dataclasses.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {

    /**
     * This method runs BEFORE the controller method
     * It extracts user info from headers and stores in ThreadLocal
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        log.debug("=== UserContextInterceptor - preHandle ===");
        log.debug("Request URI: {}", request.getRequestURI());

        // Extract headers added by API Gateway
        String userId = request.getHeader("X-User-Id");
        String email = request.getHeader("X-User-Email");
        String roles = request.getHeader("X-User-Roles");

        log.debug("Extracted headers - userId: {}, email: {}, roles: {}", userId, email, roles);

        // If headers are present, create and store UserContext
        if (userId != null) {
            UserContext context = UserContext.builder()
                    .userId(userId)
                    .email(email)
                    .roles(roles)
                    .build();

            // Store in ThreadLocal
            UserContextHolder.setUserContext(context);

            log.info("User context set for request: userId={}, email={}", userId, email);
        } else {
            log.warn("No X-User-Id header found. User context not set.");
        }

        // Return true to continue processing the request
        return true;
    }

    /**
     * This method runs AFTER the controller method completes
     * It clears the ThreadLocal to prevent memory leaks
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

        log.debug("=== UserContextInterceptor - afterCompletion ===");

        // CRITICAL: Clear ThreadLocal after request completes
        UserContextHolder.clear();

        log.debug("User context cleared");
    }
}
