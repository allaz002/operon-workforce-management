package com.operon.workforce.shift;

import java.time.Instant;

public record ShiftResponse(
        Long id,
        Instant startTime,
        Instant endTime,
        String role,
        Integer requiredEmployees,
        String location,
        String note,
        Instant createdAt
) {
}
