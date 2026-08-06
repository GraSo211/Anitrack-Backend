package com.graso.anitrack.user.client.myanimelist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseTokenRequest(
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        int expiresIn,
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
