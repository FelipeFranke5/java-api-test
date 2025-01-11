package com.felipefranke.jira_incidents.api.jira_case;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraCaseRepository extends JpaRepository<JiraCase, UUID> {}
