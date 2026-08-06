package com.graso.anitrack.user.model;

import java.util.Date;

public record RandomUserJikan(
        String profileUrl,
        String username,
        String imageUrl,
        Date lastOnline
) {
}
