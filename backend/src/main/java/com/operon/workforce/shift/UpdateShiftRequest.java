package com.operon.workforce.shift;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateShiftRequest(
        @NotNull
        Instant startTime,

        @NotNull
        Instant endTime,

        @NotBlank
        @Size(max = 100)
        String role,

        @NotNull
        @Min(1)
        Integer requiredEmployees,

        @NotBlank
        @Size(max = 150)
        String location,

        @Size(max = 500)
        String note
) {
}
