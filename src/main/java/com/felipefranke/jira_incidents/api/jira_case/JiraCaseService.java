package com.felipefranke.jira_incidents.api.jira_case;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JiraCaseService {

    private final JiraCaseRepository jiraCaseRepository;

    public JiraCaseService(JiraCaseRepository jiraCaseRepository) {
        this.jiraCaseRepository = jiraCaseRepository;
    }

    public JiraCase getJiraCase(UUID id) {
        return jiraCaseRepository.findById(id).orElseThrow(() -> new JiraCaseNotFoundException());
    }

    public JiraCase saveJiraCase(JiraCase jiraCase) {
        return jiraCaseRepository.save(jiraCase);
    }

    public List<JiraCase> getJiraCaseList() {
        return jiraCaseRepository.findAll();
    }
}
