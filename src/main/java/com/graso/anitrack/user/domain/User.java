package com.graso.anitrack.user.domain;

import com.graso.anitrack.user.domain.valueobject.Statistics;
import lombok.Builder;
import lombok.Data;

@Data
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

    private Statistics statistics;

}