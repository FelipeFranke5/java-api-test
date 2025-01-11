package com.felipefranke.jira_incidents.api.authentication;

public class AuthorizationHeaderIsNullException extends RuntimeException {

    public AuthorizationHeaderIsNullException(String message) {
        super(message);
    }
}
