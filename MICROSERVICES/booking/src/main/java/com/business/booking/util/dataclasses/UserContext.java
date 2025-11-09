package com.business.booking.util.dataclasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private String userId;
    private String email;
    private String roles;

    // Helper method to get roles as a Set
    public Set<String> getRoleSet() {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    // Helper method to check if user has a specific role
    public boolean hasRole(String role) {
        return getRoleSet().contains(role);
    }

    // Convenience methods
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isUser() {
        return hasRole("USER");
    }
}
