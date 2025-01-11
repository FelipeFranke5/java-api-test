package com.felipefranke.jira_incidents.api.jira_case;

import java.time.Instant;
import java.util.Date;

public class JiraUtil {

    private final Date jiraCreationDate;

    public JiraUtil(Date jiraCreationDate) {
        this.jiraCreationDate = jiraCreationDate;
    }

    public Date calculateExpirationDate() {
        Instant creationDateInstant = jiraCreationDate.toInstant();
        return Date.from(creationDateInstant.plusSeconds(604800L));
    }
}
