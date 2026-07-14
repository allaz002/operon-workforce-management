package com.operon.workforce.shiftassignment;

import jakarta.validation.constraints.NotNull;

public record CreateShiftAssignmentRequest(
        @NotNull
        Long userId
) {
}
