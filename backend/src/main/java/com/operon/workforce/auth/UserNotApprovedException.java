package com.operon.workforce.auth;

public class UserNotApprovedException extends RuntimeException {
    public UserNotApprovedException() {
        super("User is not approved");
    }
}
