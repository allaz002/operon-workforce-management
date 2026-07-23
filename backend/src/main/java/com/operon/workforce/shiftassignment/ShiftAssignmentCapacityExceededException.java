package com.operon.workforce.shiftassignment;

public class ShiftAssignmentCapacityExceededException extends RuntimeException {

    public static final String MESSAGE = "Shift assignment capacity exceeded!";

    public ShiftAssignmentCapacityExceededException() {
        super(MESSAGE);
    }
}
