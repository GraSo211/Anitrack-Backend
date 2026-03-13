package com.graso.anitrack.user.infrastructure.adapter;

import com.graso.anitrack.external.myanimelist.MyAnimeListApiClient;
import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;
import com.graso.anitrack.user.application.port.out.AuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MalAuthAdapter implements AuthPort {

    private final MyAnimeListApiClient myAnimeListApiClient;
    private final String clientId;
    private final String clientSecret;
    private final String backendUrl;

    public MalAuthAdapter(
            MyAnimeListApiClient myAnimeListApiClient,
            @Value("${mal.client-id}") String clientId,
            @Value("${mal.client-secret}") String clientSecret,
            @Value("${application.backend-url}") String backendUrl
    ) {
        this.myAnimeListApiClient = myAnimeListApiClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.backendUrl = backendUrl;
    }

    @Override
    public ResponseTokenRequest loginUser(String code, String codeVerifier) {
        String url = backendUrl + "/auth/mal/login";
        ResponseTokenRequest token = myAnimeListApiClient.getBearerToken(clientId, clientSecret, code, url, codeVerifier);
        System.out.println(token.accessToken());

        return token;
    }
}