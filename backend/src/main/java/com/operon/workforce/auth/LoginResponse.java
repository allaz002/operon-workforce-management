package com.operon.workforce.auth;

import com.operon.workforce.user.UserApprovalStatus;
import com.operon.workforce.user.UserRole;

public record LoginResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        UserApprovalStatus approvalStatus
) {
}
