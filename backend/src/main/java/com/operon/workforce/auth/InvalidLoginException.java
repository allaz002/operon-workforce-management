package com.operon.workforce.auth;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("Invalid email or password");
    }
}
