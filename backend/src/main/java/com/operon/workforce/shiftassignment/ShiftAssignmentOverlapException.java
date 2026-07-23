package com.operon.workforce.shiftassignment;

public class ShiftAssignmentOverlapException extends RuntimeException {

    public static final String MESSAGE = "User is already assigned to an overlapping shift!";

    public ShiftAssignmentOverlapException() {
        super(MESSAGE);
    }
}
