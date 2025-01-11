package com.felipefranke.jira_incidents.api.role;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleConfig implements CommandLineRunner {

    private final RoleRepository repository;

    public RoleConfig(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (!repository.existsByName("ADMIN")) {
            repository.save(new Role("ADMIN"));
        }

        if (!repository.existsByName("USER")) {
            repository.save(new Role("USER"));
        }
    }
}
