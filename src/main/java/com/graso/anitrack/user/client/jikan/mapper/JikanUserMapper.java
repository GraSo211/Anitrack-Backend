package com.graso.anitrack.user.client.jikan.mapper;

import com.graso.anitrack.user.client.jikan.dto.ResponseUserByIdJikanDto;
import com.graso.anitrack.user.client.jikan.dto.ResponseUsersJikanDto;
import com.graso.anitrack.user.model.JikanStatistics;
import com.graso.anitrack.user.model.RandomUserJikan;
import com.graso.anitrack.user.model.UserJikan;
import org.springframework.stereotype.Component;

@Component
public class JikanUserMapper {
    public RandomUserJikan toRandomUserJikan(ResponseUsersJikanDto.Data data) {
        var image = data.images().webp() != null
                ? data.images().webp().image_url()
                : data.images().jpg().image_url();
        return new RandomUserJikan(
                data.url(),
                data.username(),

                image,
                data.last_online());

    }

    public UserJikan toUser(ResponseUserByIdJikanDto responseUser) {
        var data = responseUser.data();
        var image = data.images().webp() != null
                ? data.images().webp().image_url()
                : data.images().jpg().image_url();
        return new UserJikan(
                data.mal_id(),
                data.username(),
                data.url(),
                image,
                data.last_online(),
                data.gender(),
                data.birthday(),
                data.location(),
                data.joined(),
                toStatistics(data.statistics()),
                data.external() != null ? java.util.Arrays.asList(data.external()) : null
        );
    }


    private JikanStatistics toStatistics(ResponseUserByIdJikanDto.Data.Statistics statistics) {
        return new JikanStatistics(
                statistics.anime().days_watched(),
                statistics.anime().mean_score(),
                statistics.anime().watching(),
                statistics.anime().completed(),
                statistics.anime().on_hold(),
                statistics.anime().dropped(),
                statistics.anime().plan_to_watch(),
                statistics.anime().total_entries(),
                statistics.anime().rewatched(),
                statistics.anime().episodes_watched()
        );
    }
}
