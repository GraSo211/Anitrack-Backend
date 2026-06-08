package com.graso.anitrack.user.domain.userjikan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserJikan {
    int malId;
    String username;
    String url;
    String imageUrl;
    String lastOnline;
    String gender;
    String birthday;
    String location;
    String joined;
    Statistics statistics;
    List<External> external;

}
