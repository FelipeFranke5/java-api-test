package com.felipefranke.jira_incidents.api.ticket;

public class TicketNullFieldException extends RuntimeException {

    public TicketNullFieldException(String field) {
        super(field + " cannot be null");
    }
}
