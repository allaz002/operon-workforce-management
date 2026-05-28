package com.operon.workforce.user;

import java.time.Instant;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        UserApprovalStatus approvalStatus,
        Instant createdAt
) {
}
