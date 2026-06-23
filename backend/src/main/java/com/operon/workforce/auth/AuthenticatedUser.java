package com.operon.workforce.auth;

public record AuthenticatedUser(
        Long userId,
        String email,
        String role
) {
}
