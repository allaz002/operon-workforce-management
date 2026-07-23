package com.operon.workforce.shiftassignment;

public class ShiftAssignmentAlreadyExistsException extends RuntimeException {

    static final public String MESSAGE = "Shift assignment already exists!";

    public ShiftAssignmentAlreadyExistsException() {
        super(MESSAGE);
    }
}
