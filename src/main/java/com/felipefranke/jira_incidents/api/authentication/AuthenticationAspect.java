package com.felipefranke.jira_incidents.api.authentication;

import com.felipefranke.jira_incidents.api.user.User;
import com.felipefranke.jira_incidents.api.user.UserService;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthenticationAspect {

    private final UserService userService;

    public AuthenticationAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(com.felipefranke.jira_incidents.api.authentication.AuthenticateHeader)")
    public void validateHeader(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        UUID userId = null;
        String authorizationHeader = null;
        String acceptHeader = null;

        for (Object arg : args) {
            if (arg instanceof UUID) {
                userId = (UUID) arg;
            } else if (arg instanceof String && authorizationHeader == null) {
                authorizationHeader = (String) arg;
            } else if (arg instanceof String) {
                acceptHeader = (String) arg;
            }
        }

        if (userId == null) {
            throw new IllegalArgumentException("missing userId.");
        }

        if (authorizationHeader == null) {
            throw new InvalidAuthorizationHeaderException("missing authorization header");
        }

        if (acceptHeader == null) {
            throw new InvalidAcceptHeaderException("missing accept header");
        }

        User user = userService.getOneUserById(userId);
        HeaderAuthentication headerAuthentication = new HeaderAuthentication(user, authorizationHeader);
        headerAuthentication.validateHeader();
    }
}
