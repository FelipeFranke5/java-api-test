package com.felipefranke.jira_incidents.api.ticket;

public class TicketMaxLengthViolationException extends RuntimeException {

    public TicketMaxLengthViolationException(String fieldName, int expectedLength) {
        super(fieldName + " cannot have more than " + expectedLength + " characters");
    }
}
