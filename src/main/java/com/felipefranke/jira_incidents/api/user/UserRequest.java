package com.felipefranke.jira_incidents.api.user;

import java.util.Arrays;
import org.apache.commons.validator.routines.EmailValidator;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RepeatCharacterRegexRule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

public record UserRequest(String username, String password, String email) {
    public UserRequest {
        if (username == null || password == null || email == null) {
            throw new UserRequestException("username, password and email are mandatory");
        }

        username = username.trim();
        password = password.trim();
        email = email.trim();

        if (username.length() < 3 || username.length() > 12) {
            throw new UserRequestException("username must be between 3 and 12 characters");
        }

        if (password.length() < 8 || password.length() > 30) {
            throw new UserRequestException("password must be between 8 and 30 characters");
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            throw new UserRequestException("invalid Email Address.");
        }

        PasswordValidator validator = new PasswordValidator(
            Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 2),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule(),
                new RepeatCharacterRegexRule(3)
            )
        );

        RuleResult result = validator.validate(new PasswordData(password));

        if (!result.isValid()) {
            throw new UserRequestException("password is not secure. Reason: " + String.join(", ", validator.getMessages(result)));
        }
    }
}
