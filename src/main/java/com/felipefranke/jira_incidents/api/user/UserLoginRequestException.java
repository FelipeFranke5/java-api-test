package com.felipefranke.jira_incidents.api.user;

public class UserLoginRequestException extends RuntimeException {

    public UserLoginRequestException(String message) {
        super(message);
    }
}
