package com.felipefranke.jira_incidents.api.user;

public class UserInactiveException extends RuntimeException {

    public UserInactiveException(String message) {
        super(message);
    }
}
