package com.operon.workforce.availability;

import java.time.Instant;

public record AvailabilityResponse(
        Long id,
        Long userId,
        Instant startTime,
        Instant endTime,
        String note,
        Instant createdAt
) {
}
