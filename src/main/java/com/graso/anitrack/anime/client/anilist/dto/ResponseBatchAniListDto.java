package com.graso.anitrack.anime.client.anilist.dto;

import java.util.List;

public record ResponseBatchAniListDto(Data data) {

    public record Data(Page page) {
    }

    public record Page(List<ResponseFetchByIdAniListDto.Data.Media> media) {
    }
}
