package com.felipefranke.jira_incidents.api.token;

import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenRepository repository;

    public TokenService(TokenRepository repository) {
        this.repository = repository;
    }

    public void incrementUses(Token token) {
        token.setUses(token.getUses() + 1);
        repository.save(token);
    }

    public void resetUses(Token token) {
        token.setUses(0);
        repository.save(token);
    }
}
