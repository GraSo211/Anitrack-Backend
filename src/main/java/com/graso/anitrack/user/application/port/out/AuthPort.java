package com.graso.anitrack.user.application.port.out;


import com.graso.anitrack.external.myanimelist.dto.ResponseTokenRequest;

public interface AuthPort {

    ResponseTokenRequest loginUser(String code, String codeVerifier);

}