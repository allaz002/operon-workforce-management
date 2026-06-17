package com.operon.workforce.user;

public class UserNotFoundException extends RuntimeException {

    public static final String errorMessage = "User not found";

    public UserNotFoundException() {
        super(errorMessage);
    }
}
