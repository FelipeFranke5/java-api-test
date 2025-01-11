package com.felipefranke.jira_incidents.api.ticket;

public class TicketMinLengthViolationException extends RuntimeException {

    public TicketMinLengthViolationException(String fieldName, int expectedLength) {
        super(fieldName + " should have at least " + expectedLength + " characters");
    }
}
