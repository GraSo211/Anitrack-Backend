package com.graso.anitrack.user.application.dto;

public record OAuthAuthorization(
        String authorizationUrl,
        String state,
        String codeVerifier
) {
}