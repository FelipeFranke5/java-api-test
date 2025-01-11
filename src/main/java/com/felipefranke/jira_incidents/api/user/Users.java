package com.felipefranke.jira_incidents.api.user;

import java.util.List;

public record Users(long count, List<UserResponse> users) {}
