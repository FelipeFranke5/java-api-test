package com.felipefranke.jira_incidents.api.user;

public record UserResponse(Long id, String username, String email, boolean isActive) {}
