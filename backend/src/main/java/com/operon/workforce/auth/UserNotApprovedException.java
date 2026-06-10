package com.operon.workforce.auth;

public class UserNotApprovedException extends RuntimeException {

    public static final String errorMessage = "User is not approved";

    public UserNotApprovedException() {
        super(errorMessage);
    }
}
