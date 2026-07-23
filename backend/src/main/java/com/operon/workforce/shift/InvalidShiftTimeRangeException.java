package com.operon.workforce.shift;

public class InvalidShiftTimeRangeException extends RuntimeException {
    public static final String MESSAGE = "Shift end time must be after start time";

    public InvalidShiftTimeRangeException() {
        super(MESSAGE);
    }
}
