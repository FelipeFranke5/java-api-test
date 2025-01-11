package com.felipefranke.jira_incidents.api.exceptionhandler;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement
public record GlobalExceptionResponse(String message, String detail) {}
