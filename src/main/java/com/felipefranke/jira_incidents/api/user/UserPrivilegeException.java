package com.felipefranke.jira_incidents.api.user;

public class UserPrivilegeException extends RuntimeException {

    public UserPrivilegeException() {
        super("resource is protected");
    }
}
