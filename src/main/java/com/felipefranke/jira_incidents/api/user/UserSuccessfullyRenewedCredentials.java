package com.felipefranke.jira_incidents.api.user;

import java.time.ZonedDateTime;

public record UserSuccessfullyRenewedCredentials(Long id, String message, ZonedDateTime time) {}
