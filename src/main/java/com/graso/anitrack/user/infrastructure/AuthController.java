package com.graso.anitrack.user.infrastructure;

import com.graso.anitrack.user.application.port.in.LoginWithMyAnimeListUseCase;
import com.graso.anitrack.user.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

    LoginWithMyAnimeListUseCase loginWithMyAnimeListUseCase;

    @GetMapping("/mal/login")
    public User loginWithMAL() {
        return loginWithMyAnimeListUseCase.loginWithMyAnimeList();
    }

    ;

}
