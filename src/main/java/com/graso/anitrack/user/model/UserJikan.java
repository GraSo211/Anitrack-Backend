package com.graso.anitrack.user.model;

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
    JikanStatistics statistics;
    List<External> external;
}
