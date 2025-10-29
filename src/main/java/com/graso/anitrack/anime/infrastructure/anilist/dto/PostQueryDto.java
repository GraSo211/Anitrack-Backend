package com.graso.anitrack.anime.infrastructure.anilist.dto;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PostQueryDto {
    String query;
    private Map<String, Object> variables;
}
