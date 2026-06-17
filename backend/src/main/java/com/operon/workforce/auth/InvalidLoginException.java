package com.operon.workforce.auth;

public class InvalidLoginException extends RuntimeException {

    public static final String errorMessage = "Invalid email or password";

    public InvalidLoginException() {
        super(errorMessage);
    }
}
