package com.graso.anitrack.user.client.myanimelist;

import com.graso.anitrack.user.client.myanimelist.MyAnimeListApiClient;
import com.graso.anitrack.user.client.myanimelist.dto.ResponseTokenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyAnimeListAuthClient {

    private final MyAnimeListApiClient myAnimeListApiClient;
    private final String clientId;
    private final String clientSecret;
    private final String backendUrl;

    public MyAnimeListAuthClient(
            MyAnimeListApiClient myAnimeListApiClient,
            @Value("${mal.client-id}") String clientId,
            @Value("${mal.client-secret}") String clientSecret,
            @Value("${application.backend-url}") String backendUrl
    ) {
        this.myAnimeListApiClient = myAnimeListApiClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.backendUrl = backendUrl.replaceAll("/+$", "");
    }

    public ResponseTokenRequest loginUser(String code, String codeVerifier) {
        String url = backendUrl + "/api/v1/auth/mal/login";
        return myAnimeListApiClient.getBearerToken(clientId, clientSecret, code, url, codeVerifier);
    }
}
