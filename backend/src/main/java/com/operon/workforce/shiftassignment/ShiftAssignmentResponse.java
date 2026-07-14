package com.operon.workforce.shiftassignment;

import java.time.Instant;

public record ShiftAssignmentResponse(
        Long id,
        Long shiftId,
        Long userId,
        Instant createdAt
) {
}
