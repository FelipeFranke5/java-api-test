package com.felipefranke.jira_incidents.api.exceptionhandler;

import com.felipefranke.jira_incidents.api.authentication.AuthorizationHeaderIsNullException;
import com.felipefranke.jira_incidents.api.authentication.InvalidAcceptHeaderException;
import com.felipefranke.jira_incidents.api.authentication.InvalidAuthorizationHeaderException;
import com.felipefranke.jira_incidents.api.role.RoleNotFoundException;
import com.felipefranke.jira_incidents.api.ticket.TicketMaxLengthViolationException;
import com.felipefranke.jira_incidents.api.ticket.TicketMinLengthViolationException;
import com.felipefranke.jira_incidents.api.ticket.TicketNotFoundException;
import com.felipefranke.jira_incidents.api.ticket.TicketNullFieldException;
import com.felipefranke.jira_incidents.api.user.UserLoginRequestException;
import com.felipefranke.jira_incidents.api.user.UserNotFoundException;
import com.felipefranke.jira_incidents.api.user.UserPrivilegeException;
import com.felipefranke.jira_incidents.api.user.UserRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRequestException.class)
    public ResponseEntity<GlobalExceptionResponse> handleUserRequestException(UserRequestException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Invalid Request Body", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalExceptionResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Invalid Request Body", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handleRoleNotFoundException(RoleNotFoundException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Role Not Resolved", "unable to find a role with given params");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handleUserNotFoundException() {
        GlobalExceptionResponse response = new GlobalExceptionResponse("User Not Found", "unable to find an user with this id");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handleTicketNotFoundException() {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Ticket Not Found", "unable to find an ticket with this id");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<GlobalExceptionResponse> handleHttpRequestMethodNotSupportedException() {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Method Not Allowed", "method not allowed for this endpoint");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<GlobalExceptionResponse> handleMissingRequestHeaderException() {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Access Not Granted", "provide an authorization header");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<GlobalExceptionResponse> handleInvalidAuthorizationHeaderException(
        InvalidAuthorizationHeaderException exception
    ) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Access Not Granted", exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidAcceptHeaderException.class)
    public ResponseEntity<GlobalExceptionResponse> handleInvalidAcceptHeaderException(InvalidAcceptHeaderException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Invalid Accept Header", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AuthorizationHeaderIsNullException.class)
    public ResponseEntity<GlobalExceptionResponse> handleAuthorizationHeaderIsNullException(AuthorizationHeaderIsNullException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Null Accept Header", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<GlobalExceptionResponse> handleNoHandlerFoundException() {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Resource Not Found", "could not find resource with given URL");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TicketNullFieldException.class)
    public ResponseEntity<GlobalExceptionResponse> handleTicketNullFieldException(TicketNullFieldException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Missing Required Field", "detail: " + exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TicketMinLengthViolationException.class)
    public ResponseEntity<GlobalExceptionResponse> handleTicketMinLengthViolationException(TicketMinLengthViolationException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Invalid Field Length [min]", "detail: " + exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TicketMaxLengthViolationException.class)
    public ResponseEntity<GlobalExceptionResponse> handleTicketMaxLengthViolationException(TicketMaxLengthViolationException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Invalid Field Length [max]", "detail: " + exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<GlobalExceptionResponse> handleHttpMediaTypeNotAcceptableException(
        HttpMediaTypeNotAcceptableException exception
    ) {
        GlobalExceptionResponse response = new GlobalExceptionResponse(
            "Media Type Not Accepted",
            "change the 'Accept' header to 'application/xml''"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserPrivilegeException.class)
    public ResponseEntity<GlobalExceptionResponse> handleUserPrivilegeException(UserPrivilegeException exception) {
        GlobalExceptionResponse response = new GlobalExceptionResponse("Protected Resource", exception.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UserLoginRequestException.class)
    public ResponseEntity<GlobalExceptionResponse> handleUserLoginRequestException(UserLoginRequestException exception) {
        String message = null;

        if (exception.getMessage().contains("problem:")) {
            String[] parts = exception.getMessage().split("problem:");
            if (parts.length > 1) {
                message = parts[1].trim().toLowerCase();
            } else {
                message = "unexpected error";
            }
        } else {
            message = exception.getMessage();
        }

        GlobalExceptionResponse response = new GlobalExceptionResponse("Invalid Login Request", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String message = null;

        if (exception.getMessage().contains("Required request body is missing")) {
            message = "missing request body";
        } else {
            message = exception.getMessage();
        }

        GlobalExceptionResponse response = new GlobalExceptionResponse("Invalid Login Request", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
