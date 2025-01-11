package com.felipefranke.jira_incidents;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResponseConfig implements WebMvcConfigurer {

    // Default response type = XML

    @Override
    public void configureContentNegotiation(@SuppressWarnings("null") ContentNegotiationConfigurer contentNegotiationConfigurer) {
        contentNegotiationConfigurer.defaultContentType(MediaType.APPLICATION_XML);
    }
}
