package com.felipefranke.jira_incidents.api.ticket;

import com.felipefranke.jira_incidents.api.authentication.AuthenticateHeader;
import com.felipefranke.jira_incidents.api.user.User;
import com.felipefranke.jira_incidents.api.user.UserActivityCheck;
import com.felipefranke.jira_incidents.api.user.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/ticket", produces = "application/xml")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @UserActivityCheck
    @AuthenticateHeader
    @GetMapping(path = "/list_all/user/{userId}")
    public ResponseEntity<Tickets> listTickets(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader
    ) {
        User user = userService.getOneUserById(userId);
        List<Ticket> tickets = ticketService.getFilteredTicketList(user);
        List<TicketResponse> ticketResponses = new ArrayList<>();

        for (Ticket ticket : tickets) {
            TicketResponse ticketResponse = new TicketResponse(
                ticket.getId(),
                ticket.getUser().getName(),
                ticket.isCompleted(),
                ticket.getCreationDateTime(),
                ticket.getMailToCase(),
                ticket.getJiraCase()
            );
            ticketResponses.add(ticketResponse);
        }

        Tickets response = new Tickets(user.getName(), ticketService.getTicketCount(), ticketResponses);
        return ResponseEntity.ok(response);
    }

    @UserActivityCheck
    @AuthenticateHeader
    @GetMapping(path = "/list_one/user/{userId}/ticket/{ticketId}")
    public ResponseEntity<TicketResponse> oneTicket(
        @PathVariable Long userId,
        @PathVariable UUID ticketId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader
    ) {
        User user = userService.getOneUserById(userId);
        Ticket ticket = ticketService.getTicketByUserAndId(user, ticketId);
        TicketResponse ticketResponse = new TicketResponse(
            ticketId,
            ticket.getUser().getName(),
            ticket.isCompleted(),
            ticket.getCreationDateTime(),
            ticket.getMailToCase(),
            ticket.getJiraCase()
        );
        return ResponseEntity.ok(ticketResponse);
    }

    @UserActivityCheck
    @AuthenticateHeader
    @GetMapping(path = "/extract/email/user/{userId}")
    public ResponseEntity<Void> sendNotificationWithTickets(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader
    ) {
        User user = userService.getOneUserById(userId);
        ticketService.sendEmailNotificationWithTickets(user);
        return ResponseEntity.noContent().build();
    }

    @UserActivityCheck
    @AuthenticateHeader
    @PostMapping(path = "/create/user/{userId}/")
    public ResponseEntity<TicketResponse> saveTicket(
        @PathVariable Long userId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader,
        @RequestBody TicketRequest ticketRequest
    ) {
        User user = userService.getOneUserById(userId);
        Ticket createdTicket = ticketService.saveTicket(ticketRequest, user);
        TicketResponse ticketResponse = new TicketResponse(
            createdTicket.getId(),
            createdTicket.getUser().getName(),
            createdTicket.isCompleted(),
            createdTicket.getCreationDateTime(),
            createdTicket.getMailToCase(),
            createdTicket.getJiraCase()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketResponse);
    }

    @UserActivityCheck
    @AuthenticateHeader
    @PatchMapping(path = "/complete/user/{userId}/ticket/{ticketId}")
    public ResponseEntity<Void> markDone(
        @PathVariable Long userId,
        @PathVariable UUID ticketId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestHeader("Accept") String acceptHeader
    ) {
        Ticket ticket = ticketService.getTicket(ticketId);
        ticketService.markTicketAsCompleted(ticket);
        return ResponseEntity.noContent().build();
    }
}
