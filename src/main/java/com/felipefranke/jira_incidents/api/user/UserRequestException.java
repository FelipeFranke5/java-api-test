package com.felipefranke.jira_incidents.api.user;

public class UserRequestException extends RuntimeException {

    public UserRequestException(String message) {
        super(message);
    }
}
