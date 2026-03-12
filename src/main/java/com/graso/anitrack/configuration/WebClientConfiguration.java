package com.graso.anitrack.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient aniListWebClient() {
        return WebClient.builder()
                .baseUrl("https://graphql.anilist.co")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient jikanWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.jikan.moe/v4")
                .build();
    }


}