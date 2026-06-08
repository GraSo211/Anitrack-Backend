package com.graso.anitrack.user.domain.userjikan;

import java.util.Date;

public record RandomUserJikan(
        String profileUrl,
        String username,
        String imageUrl,
        Date lastOnline
) {
}
