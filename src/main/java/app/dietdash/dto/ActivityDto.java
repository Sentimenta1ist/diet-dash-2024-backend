package app.dietdash.dto;

import app.dietdash.enums.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ActivityDto {
    int id;
    String description;
    double multiplier;

    public static ActivityDto toActivityDto(Activity activity, String lang) {
        if (activity == null) {
            return null;
        }

        ActivityDto.ActivityDtoBuilder activityDto = ActivityDto.builder();

        activityDto.id(activity.getId());
        activityDto.description("en".equals(lang) ? activity.getEnDescription() : activity.getDescription());
        activityDto.multiplier(activity.getMultiplier());

        return activityDto.build();
    }
}
