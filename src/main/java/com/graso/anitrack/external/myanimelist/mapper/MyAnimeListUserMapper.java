package com.graso.anitrack.external.myanimelist.mapper;

import com.graso.anitrack.external.myanimelist.dto.ResponseUserRequest;
import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.domain.valueobject.Statistics;
import org.springframework.stereotype.Component;

@Component
public class MyAnimeListUserMapper {

    public User responseUserRequestToUser(ResponseUserRequest response) {
        return User.builder()
                .id(response.id()).name(response.name()).picture(response.picture()).gender(response.gender()).birthday(response.birthday()).location(response.location()).joinedAt(response.joinedAt()).timeZone(response.timeZone()).statistics(toStatistics(response.animeStatistics())).build();

    }


    private Statistics toStatistics(ResponseUserRequest.AnimeStatistics stats) {

        if (stats == null) {
            return null;
        }

        return new Statistics(
                stats.numItemsWatching(),
                stats.numItemsCompleted(),
                stats.numItemsOnHold(),
                stats.numItemsDropped(),
                stats.numItemsPlanToWatch(),
                stats.numItems(),

                stats.numDaysWatched(),
                stats.numDaysWatching(),
                stats.numDaysCompleted(),
                stats.numDaysOnHold(),
                stats.numDaysDropped(),
                stats.numDays(),

                stats.numEpisodes(),
                stats.numTimesRewatched(),
                stats.meanScore()
        );
    }
}
