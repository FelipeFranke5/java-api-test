package com.felipefranke.jira_incidents.api.user;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UserSuccessfullyRenewedCredentials(UUID id, String message, ZonedDateTime time) {}
