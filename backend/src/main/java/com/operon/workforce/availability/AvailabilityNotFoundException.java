package com.operon.workforce.availability;

public class AvailabilityNotFoundException extends RuntimeException {
    public static final String errorMessage = "Availability not found";

    public AvailabilityNotFoundException() {
        super(errorMessage);
    }
}
