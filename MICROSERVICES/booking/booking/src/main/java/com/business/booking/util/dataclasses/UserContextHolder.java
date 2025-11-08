package com.business.booking.util.dataclasses;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserContextHolder {

    // ThreadLocal variable - each thread gets its own copy
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    // Store user context in current thread
    public static void setUserContext(UserContext context) {
        log.debug("Setting user context for userId: {}", context != null ? context.getUserId() : "null");
        userContext.set(context);
    }

    // Get user context from current thread
    public static UserContext getUserContext() {
        return userContext.get();
    }

    // Convenience method - get current user ID
    public static String getCurrentUserId() {
        UserContext context = userContext.get();
        return context != null ? context.getUserId() : null;
    }

    // Convenience method - get current user email
    public static String getCurrentUserEmail() {
        UserContext context = userContext.get();
        return context != null ? context.getEmail() : null;
    }

    // Convenience method - get current user roles
    public static String getCurrentUserRoles() {
        UserContext context = userContext.get();
        return context != null ? context.getRoles() : null;
    }

    // Check if user has specific role
    public static boolean hasRole(String role) {
        UserContext context = userContext.get();
        return context != null && context.hasRole(role);
    }

    // Check if user is admin
    public static boolean isAdmin() {
        UserContext context = userContext.get();
        return context != null && context.isAdmin();
    }

    // IMPORTANT: Clear context after request completes
    public static void clear() {
        log.debug("Clearing user context for userId: {}", getCurrentUserId());
        userContext.remove();
    }
}
