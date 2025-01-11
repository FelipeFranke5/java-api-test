package com.felipefranke.jira_incidents.api.mail_to_case;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MailToCaseService {

    private final MailToCaseRepository mailToCaseRepository;

    public MailToCaseService(MailToCaseRepository mailToCaseRepository) {
        this.mailToCaseRepository = mailToCaseRepository;
    }

    public MailToCase getMailToCase(UUID id) {
        return mailToCaseRepository.findById(id).orElseThrow(() -> new MailToCaseNotFoundException());
    }

    public MailToCase saveMailToCase(MailToCase mailToCase) {
        return mailToCaseRepository.save(mailToCase);
    }

    public List<MailToCase> getMailToCaseList() {
        return mailToCaseRepository.findAll();
    }
}
