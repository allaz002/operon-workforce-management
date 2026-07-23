package com.operon.workforce.availability;

public class AvailabilityNotFoundException extends RuntimeException {
    public static final String MESSAGE = "Availability not found";

    public AvailabilityNotFoundException() {
        super(MESSAGE);
    }
}
