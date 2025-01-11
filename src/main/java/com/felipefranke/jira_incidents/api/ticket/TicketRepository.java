package com.felipefranke.jira_incidents.api.ticket;

import com.felipefranke.jira_incidents.api.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByUserOrderByCreationDateTimeDesc(User user);
    Optional<Ticket> findByUserAndId(User user, UUID id);
}
