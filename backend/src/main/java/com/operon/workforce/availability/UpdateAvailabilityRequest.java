package com.operon.workforce.availability;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateAvailabilityRequest(
        @NotNull
        Instant startTime,

        @NotNull
        Instant endTime,

        @Size(max = 500)
        String note
) {
}
