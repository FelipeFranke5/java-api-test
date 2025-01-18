package com.felipefranke.jira_incidents.api.authentication;

import com.felipefranke.jira_incidents.api.user.User;
import com.felipefranke.jira_incidents.api.user.UserCredentialsUtil;

public record HeaderAuthentication(User user, String authorizationHeader) {
    public void validateFirstPartEqualsBase64(String firstPart) {
        if (!firstPart.equals("Base64")) {
            throw new InvalidAuthorizationHeaderException("invalid authorization header type");
        }
    }

    public void validateHeaderFormat(String[] splittedHeader) {
        if (splittedHeader.length != 2) {
            throw new InvalidAuthorizationHeaderException("invalid authorization header format");
        }
    }

    public void validateAuthorizationHeaderIsNotNull() {
        if (authorizationHeader == null) {
            throw new AuthorizationHeaderIsNullException("authorization header is required");
        }
    }

    public void validateUserTokenEqualsEncodedCredentials(String requestToken, String encodedCredentials) {
        if (!requestToken.equals(encodedCredentials)) {
            throw new InvalidAuthorizationHeaderException("authentication failed");
        }
    }

    private String getString(String[] parts) {
        validateHeaderFormat(parts);
        validateFirstPartEqualsBase64(parts[0]);

        UserCredentialsUtil userCredentialsUtil = new UserCredentialsUtil(user.getClientId(), user.getClientSecret());

        return userCredentialsUtil.encodeCredentials();
    }

    public void validateHeader() {
        validateAuthorizationHeaderIsNotNull();
        String[] parts = authorizationHeader.split(" ", 2);
        String encodedCredentials = getString(parts);
        validateUserTokenEqualsEncodedCredentials(parts[1], encodedCredentials);
    }
}
