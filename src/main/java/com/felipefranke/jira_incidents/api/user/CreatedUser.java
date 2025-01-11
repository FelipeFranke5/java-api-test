package com.felipefranke.jira_incidents.api.user;

import java.time.ZonedDateTime;

public record CreatedUser(ZonedDateTime time, UserResponse user) {}
