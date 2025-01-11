package com.felipefranke.jira_incidents.api.mail_to_case;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailToCaseRepository extends JpaRepository<MailToCase, UUID> {}
