package com.felipefranke.jira_incidents.api.user;

import java.util.UUID;

public record UserResponse(UUID id, String username, String email, boolean isActive) {}
