package com.operon.workforce.availability;

public class InvalidAvailabilityTimeRangeException extends RuntimeException {
    public static final String errorMessage = "Availability end time must be after start time";

    public InvalidAvailabilityTimeRangeException() {
        super(errorMessage);
    }
}
