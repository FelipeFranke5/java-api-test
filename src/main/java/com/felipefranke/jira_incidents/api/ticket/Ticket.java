package com.felipefranke.jira_incidents.api.ticket;

import com.felipefranke.jira_incidents.api.jira_case.JiraCase;
import com.felipefranke.jira_incidents.api.mail_to_case.MailToCase;
import com.felipefranke.jira_incidents.api.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column
    private boolean completed;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDateTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mail_to_case_id")
    private MailToCase mailToCase;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "jira_case_id")
    private JiraCase jiraCase;
}
