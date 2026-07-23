package com.operon.workforce.user;

public class DuplicateEmailException extends RuntimeException {

    public static final String MESSAGE = "Email is already registered";

    public DuplicateEmailException() {
        super(MESSAGE);
    }
}
