package com.felipefranke.jira_incidents.api.authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // <- apply to methods
@Retention(RetentionPolicy.RUNTIME) // <- available at runtime
public @interface AuthenticateHeader {
}
