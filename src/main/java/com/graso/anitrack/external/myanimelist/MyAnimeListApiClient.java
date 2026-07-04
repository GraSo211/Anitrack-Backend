package com.graso.anitrack.external.myanimelist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graso.anitrack.external.myanimelist.dto.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@AllArgsConstructor
public class MyAnimeListApiClient {
    private static final Logger log = LoggerFactory.getLogger(MyAnimeListApiClient.class);
    private WebClient myAnimeListWebClientFirstVersion;
    private WebClient myAnimeListWebClientSecondVersion;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseTokenRequest getBearerToken(
            String clientId,
            String clientSecret,
            String code,
            String redirectUri,
            String codeVerifier
    ) {
        log.info("Token exchange: clientId={}, code={}, redirectUri={}, codeVerifier={}",
                clientId, code, redirectUri, codeVerifier);

        try {
            String formBody = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                    + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                    + "&grant_type=authorization_code"
                    + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                    + "&code_verifier=" + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8);

            log.info("Form body (masked): client_id={}&client_secret=***&grant_type=authorization_code&code={}&redirect_uri={}&code_verifier={}",
                    clientId, code, URLEncoder.encode(redirectUri, StandardCharsets.UTF_8), URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8));

            URL url = URI.create("https://myanimelist.net/v1/oauth2/token").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            byte[] bodyBytes = formBody.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(bodyBytes.length);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(bodyBytes);
            }

            int responseCode = conn.getResponseCode();
            log.info("MAL response status: {}", responseCode);

            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String json = br.lines().collect(Collectors.joining());
                    log.info("MAL success response: {}", json);
                    return objectMapper.readValue(json, ResponseTokenRequest.class);
                }
            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorBody = br.lines().collect(Collectors.joining());
                    log.error("MAL error response: {} - {}", responseCode, errorBody);
                    throw new RuntimeException("MAL token exchange failed (" + responseCode + "): " + errorBody);
                }
            }
        } catch (Exception e) {
            log.error("Token exchange failed", e);
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("Token exchange failed", e);
        }
    }

    public ResponseUserRequest getMyUser(String token) {
        return myAnimeListWebClientSecondVersion.get()
                .uri("/v2/users/@me?fields=anime_statistics")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseUserRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeListRequest getAnimeList(String token, String status) {
        String firstUri = buildAnimeListUri(status);
        ResponseAnimeListRequest response = fetchAnimeListPage(token, firstUri);

        List<ResponseAnimeListRequest.Data> allData = new ArrayList<>(response.data());
        String nextUrl = response.paging() != null ? response.paging().next() : null;

        while (nextUrl != null && !nextUrl.isEmpty()) {
            URI nextUri = URI.create(nextUrl);
            String nextPathAndQuery = nextUri.getPath() + "?" + nextUri.getQuery();
            response = fetchAnimeListPage(token, nextPathAndQuery);
            allData.addAll(response.data());
            nextUrl = response.paging() != null ? response.paging().next() : null;
        }

        return new ResponseAnimeListRequest(allData, null);
    }

    private String buildAnimeListUri(String status) {
        StringBuilder uri = new StringBuilder("/v2/users/@me/animelist?fields=list_status&limit=1000");
        if (status != null && !status.isEmpty()) {
            uri.append("&status=").append(status);
        }
        return uri.toString();
    }

    private ResponseAnimeListRequest fetchAnimeListPage(String token, String uri) {
        return myAnimeListWebClientSecondVersion.get()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeListRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeStatusRequest getAnimeStatus(String token, int id) {
        return myAnimeListWebClientSecondVersion.get()
                .uri(String.format("/v2/anime/%d?fields=my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeStatusRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeToListRequest addAnimeToList(String token, int id) {
        return myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("/v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ResponseAnimeToListRequest.class)
                .block(Duration.ofSeconds(10));
    }

    public ResponseAnimeToListRequest modifyAnimeToList(String token, int id, String status, int score, int numEpisodes) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("status", status);
        body.add("score", String.valueOf(score));
        body.add("num_watched_episodes", String.valueOf(numEpisodes));

        return myAnimeListWebClientSecondVersion.patch()
                .uri(String.format("/v2/anime/%d/my_list_status", id))
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .body(BodyInserters.fromFormData(body))
                .retrieve()
.bodyToMono(ResponseAnimeToListRequest.class)
                .block(Duration.ofSeconds(10));
}
}
