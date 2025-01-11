package com.felipefranke.jira_incidents.api.ticket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.validator.routines.EmailValidator;

public record TicketRequest(
    String caseNumber,
    String caseSubject,
    String suppliedEmail,
    String suppliedName,
    String jiraProtocol,
    String jiraName,
    String jiraCreationDate
) {
    private void validateNullField(String fieldName, Object field) {
        if (field == null) {
            throw new TicketNullFieldException(fieldName);
        }
    }

    private void validateMinLength(String fieldName, String field, int expectedLength) {
        if (field.length() < expectedLength) {
            throw new TicketMinLengthViolationException(fieldName, expectedLength);
        }
    }

    private void validateMaxLength(String fieldName, String field, int expectedLength) {
        if (field.length() > expectedLength) {
            throw new TicketMaxLengthViolationException(fieldName, expectedLength);
        }
    }

    private void validateEmail(String field) {
        if (!EmailValidator.getInstance().isValid(field)) {
            throw new IllegalArgumentException("invalid Email Address");
        }
    }

    private void validateStartsWithRitmOrInc(String field) {
        if (!field.startsWith("RITM-") && (!field.startsWith("INC-"))) {
            throw new IllegalArgumentException("jiraProtocol must start with either 'INC-' or 'RITM-'");
        }
    }

    private void validateDateIsBeforeNow(Date field) {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        if (field.toInstant().isAfter(now)) {
            throw new IllegalArgumentException("jiraCreationDate cannot be set in a future date");
        }
    }

    private void validateFields(String fieldName, String value, Object valueObject) {
        switch (fieldName) {
            case "caseNumber":
                validateMinLength(fieldName, value, 3);
                validateMaxLength(fieldName, value, 8);
                break;
            case "caseSubject":
                validateMinLength(fieldName, value, 8);
                validateMaxLength(fieldName, value, 100);
                break;
            case "suppliedEmail":
                validateEmail(value);
                break;
            case "suppliedName":
                validateMinLength(fieldName, value, 3);
                validateMaxLength(fieldName, value, 30);
                break;
            case "jiraProtocol":
                validateMinLength(fieldName, value, 6);
                validateMaxLength(fieldName, value, 10);
                validateStartsWithRitmOrInc(value);
                break;
            case "jiraName":
                validateMinLength(fieldName, value, 10);
                validateMaxLength(fieldName, value, 100);
                break;
            case "jiraCreationDate":
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = (String) valueObject;
                Date dateValue = null;

                try {
                    dateValue = dateFormatter.parse(dateString);
                } catch (ParseException exception) {
                    throw new IllegalArgumentException("jiraCreationDate must follow this format: 'yyyy-MM-dd'");
                }

                validateDateIsBeforeNow(dateValue);
                break;
        }
    }

    public TicketRequest {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("caseNumber", caseNumber);
        fields.put("caseSubject", caseSubject);
        fields.put("suppliedEmail", suppliedEmail);
        fields.put("suppliedName", suppliedName);
        fields.put("jiraProtocol", jiraProtocol);
        fields.put("jiraName", jiraName);
        fields.put("jiraCreationDate", jiraCreationDate);

        for (Map.Entry<String, Object> fieldsEntry : fields.entrySet()) {
            // Check for null
            validateNullField(fieldsEntry.getKey(), fieldsEntry.getValue());

            String value = (String) fieldsEntry.getValue();
            Object valueObject = fieldsEntry.getValue();

            // Perform other validations
            validateFields(fieldsEntry.getKey(), value, valueObject);
        }
    }
}
