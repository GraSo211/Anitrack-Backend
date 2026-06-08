package com.graso.anitrack.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    private HttpClient createHttpClient() {
        ConnectionProvider provider = ConnectionProvider.builder("anitrack-pool")
                .maxConnections(50)
                .pendingAcquireMaxCount(100)
                .pendingAcquireTimeout(Duration.ofSeconds(10))
                .maxIdleTime(Duration.ofSeconds(30))
                .build();

        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));
    }

    @Bean
    public WebClient aniListWebClient() {
        return WebClient.builder()
                .baseUrl("https://graphql.anilist.co")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }

    @Bean
    public WebClient jikanWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.jikan.moe/v4")
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }

    @Bean
    public WebClient myAnimeListWebClientFirstVersion() {
        return WebClient.builder()
                .baseUrl("https://myanimelist.net/")
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }

    @Bean
    public WebClient myAnimeListWebClientSecondVersion() {
        return WebClient.builder()
                .baseUrl("https://api.myanimelist.net/")
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }
}
