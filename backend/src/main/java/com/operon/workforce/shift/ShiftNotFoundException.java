package com.operon.workforce.shift;

public class ShiftNotFoundException extends RuntimeException {
    public static final String MESSAGE = "Shift not found.";

    public ShiftNotFoundException() {
        super(MESSAGE);
    }
}
