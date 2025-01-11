package com.felipefranke.jira_incidents.api.ticket;

import com.felipefranke.jira_incidents.api.jira_case.JiraCase;
import com.felipefranke.jira_incidents.api.mail_to_case.MailToCase;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    String owner,
    boolean completed,
    LocalDateTime creationDateTime,
    MailToCase mailToCase,
    JiraCase jiraCase
) {}
