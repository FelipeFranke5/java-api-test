package com.felipefranke.jira_incidents.api.ticket;

import java.util.List;

public record Tickets(String owner, long count, List<TicketResponse> tickets) {}
