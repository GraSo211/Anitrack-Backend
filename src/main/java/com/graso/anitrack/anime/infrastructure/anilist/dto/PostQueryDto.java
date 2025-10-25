package com.graso.anitrack.anime.infrastructure.anilist.dto;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostQueryDto {
    String query;
    Object variables;
}
