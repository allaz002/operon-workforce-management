package com.operon.workforce.auth;

public class UserNotApprovedException extends RuntimeException {

    public static final String MESSAGE = "User is not approved";

    public UserNotApprovedException() {
        super(MESSAGE);
    }
}
