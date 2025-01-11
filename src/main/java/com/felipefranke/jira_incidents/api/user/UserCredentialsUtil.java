package com.felipefranke.jira_incidents.api.user;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public record UserCredentialsUtil(UUID clientId, UUID clientSecret) {
    public String encodeCredentials() {
        String credentials = this.clientId().toString() + ":" + this.clientSecret().toString();
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    public String decodeCredentials(String encodedCredentials) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        String[] parts = decodedString.split(":", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Credentials Format.");
        }

        return decodedString;
    }

    public String encodePassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
    }

    public String decodePassword(String encodedPassword) {
        return Arrays.toString(Base64.getDecoder().decode(encodedPassword));
    }
}
