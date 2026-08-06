package com.graso.anitrack.user.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {

    private Integer id;
    private String name;
    private String picture;
    private String gender;
    private String birthday;
    private String location;
    private String joinedAt;
    private String timeZone;

    private MalStatistics statistics;
}
