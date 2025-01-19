package com.felipefranke.jira_incidents.api.ticket;

import com.felipefranke.jira_incidents.api.jira_case.JiraCase;
import com.felipefranke.jira_incidents.api.jira_case.JiraCaseService;
import com.felipefranke.jira_incidents.api.jira_case.JiraUtil;
import com.felipefranke.jira_incidents.api.mail_to_case.MailToCase;
import com.felipefranke.jira_incidents.api.mail_to_case.MailToCaseService;
import com.felipefranke.jira_incidents.api.user.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final MailToCaseService mailToCaseService;
    private final JiraCaseService jiraCaseService;
    private final JavaMailSender mailSender;
    private final Environment environment;

    public TicketService(
        TicketRepository ticketRepository,
        MailToCaseService mailToCaseService,
        JiraCaseService jiraCaseService,
        JavaMailSender mailSender,
        Environment environment
    ) {
        this.ticketRepository = ticketRepository;
        this.mailToCaseService = mailToCaseService;
        this.jiraCaseService = jiraCaseService;
        this.mailSender = mailSender;
        this.environment = environment;
    }

    public long getTicketCount() {
        return ticketRepository.count();
    }

    public Ticket getTicket(UUID id) {
        verifyIdIsValid(id);
        return ticketRepository.findById(id).orElseThrow(TicketNotFoundException::new);
    }

    private static void verifyIdIsValid(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null or empty.");
        }
    }

    public Ticket getTicketByUserAndId(User user, UUID id) {
        return ticketRepository.findByUserAndId(user, id).orElseThrow(TicketNotFoundException::new);
    }

    public List<Ticket> getTicketList() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getFilteredTicketList(User user) {
        return ticketRepository.
                findByUserOrderByCreationDateTimeDesc(user).stream()
                    .filter(ticket -> !ticket.isCompleted())
                    .toList();
    }

    public void markTicketAsCompleted(Ticket ticket) {
        checkIfTicketIsNullOrCompleted(ticket);
        ticket.setCompleted(true);
        ticketRepository.save(ticket);
    }

    private static void checkIfTicketIsNullOrCompleted(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("the ticket cannot be null or empty");
        }

        if (ticket.isCompleted()) {
            throw new IllegalArgumentException("ticket already completed");
        }
    }

    public void saveTicket(TicketRequest request, User authenticatedUser) {
        validateUserAndRequest(request, authenticatedUser);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date jiraCreationDate;
        jiraCreationDate = getJiraCreationDate(request, dateFormatter);
        JiraUtil jiraUtil = new JiraUtil(jiraCreationDate);

        MailToCase newMailToCase = MailToCase.builder()
            .caseNumber(request.caseNumber())
            .caseSubject(request.caseSubject())
            .suppliedEmail(request.suppliedEmail())
            .suppliedName(request.suppliedName())
            .build();
        newMailToCase = mailToCaseService.saveMailToCase(newMailToCase);

        JiraCase newJiraCase = JiraCase.builder()
            .jiraProtocol(request.jiraProtocol())
            .jiraName(request.jiraName())
            .jiraCreationDate(jiraCreationDate)
            .jiraExpiresAt(jiraUtil.calculateExpirationDate())
            .build();
        newJiraCase = jiraCaseService.saveJiraCase(newJiraCase);

        Ticket newTicket = Ticket.builder()
            .user(authenticatedUser)
            .completed(false)
            .mailToCase(newMailToCase)
            .jiraCase(newJiraCase)
            .build();

        ticketRepository.save(newTicket);
    }

    private static Date getJiraCreationDate(TicketRequest request, SimpleDateFormat dateFormatter) {
        Date jiraCreationDate;
        try {
            jiraCreationDate = dateFormatter.parse(request.jiraCreationDate());
        } catch (ParseException exception) {
            throw new IllegalArgumentException("jiraCreationDate must follow this format: 'yyyy-MM-dd'");
        }
        return jiraCreationDate;
    }

    private static void validateUserAndRequest(TicketRequest request, User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new IllegalArgumentException("cannot save ticket for empty user.");
        }

        if (request == null) {
            throw new IllegalArgumentException("the request cannot be empty or null.");
        }
    }

    public void sendMail(User user, String subject, StringBuilder body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmailAddress());
        message.setSubject(subject);
        message.setText(body.toString());
        message.setFrom(environment.getProperty("spring.mail.username"));
        mailSender.send(message);
    }

    public StringBuilder buildMessageWithTickets(User user) {
        List<Ticket> tickets = getFilteredTicketList(user);
        StringBuilder messageBody = new StringBuilder();
        tickets.forEach(ticket -> messageBody
                .append("SF: ")
                .append(ticket.getMailToCase().getCaseNumber())
                .append(" - JIRA: ").append(ticket.getJiraCase().getJiraProtocol())
                .append("\n"));
        return messageBody;
    }

    public void sendEmailNotificationWithTickets(User user) {
        Instant now = Instant.now();
        StringBuilder messageBody = buildMessageWithTickets(user);
        sendMail(user, now.toString(), messageBody);
    }
}
