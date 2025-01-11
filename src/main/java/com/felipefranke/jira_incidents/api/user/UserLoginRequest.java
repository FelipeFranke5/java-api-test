package com.felipefranke.jira_incidents.api.user;

public record UserLoginRequest(String username, String password) {
    public UserLoginRequest {
        if (username == null) {
            throw new UserLoginRequestException("username is required");
        }

        if (password == null) {
            throw new UserLoginRequestException("password is required");
        }
    }
}
