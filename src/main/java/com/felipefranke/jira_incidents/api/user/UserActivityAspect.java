package com.felipefranke.jira_incidents.api.user;

import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserActivityAspect {

    private final UserService userService;

    public UserActivityAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(com.felipefranke.jira_incidents.api.user.UserActivityCheck)")
    public void validateUserIsActive(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID userId = null;

        for (Object arg : args) {
            if (arg instanceof Long) {
                userId = (UUID) arg;
                break;
            }
        }

        if (userId == null) {
            throw new IllegalArgumentException("missing userId.");
        }

        User user = userService.getOneUserById(userId);

        if (!user.isActive()) {
            throw new UserInactiveException("this user is no longer active");
        }
    }
}
