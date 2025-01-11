package com.felipefranke.jira_incidents.api.user;

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
        Long userId = null;

        for (Object arg : args) {
            if (arg instanceof Long) {
                userId = (Long) arg;
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
