package com.operon.workforce.shiftassignment;

public class ShiftAssignmentNotFoundException extends RuntimeException {

    static final public String message = "Shift assignment not found!";

    public ShiftAssignmentNotFoundException() {
        super(message);
    }
}
