package app.dietdash.dto;

import app.dietdash.enums.Activity;
import app.dietdash.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static app.dietdash.dto.ActivityDto.toActivityDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileDto {
    private String name;
    private int age;
    private String gender;
    private int height;
    private double weight;
    private int waist;
    private int hips;
    private int goalWeight;
    private ActivityDto activity;
    private double bodyMassIndex;
    private int dailyCalorieNorm;
    private int selectedCalorieLimit;
    private int dailyProteinLimit;
    private int dailyFatLimit;
    private int dailyCarbsLimit;
    private double dailyProteinPercentage;
    private double dailyFatPercentage;
    private double dailyCarbsPercentage;

    public static ProfileDto toProfileDto(Profile profile, String lang) {
        ProfileDto dto = new ProfileDto();

        dto.name = profile.getName();
        dto.age = profile.getAge();
        dto.gender = profile.getGender();
        dto.height = profile.getHeight();
        dto.weight = profile.getWeight();
        dto.waist = profile.getWaist();
        dto.hips = profile.getHips();
        dto.goalWeight = profile.getGoalWeight();
        dto.activity = toActivityDto(profile.getActivity(), lang);
        dto.bodyMassIndex = profile.getBodyMassIndex();
        dto.dailyCalorieNorm = profile.getDailyCalorieNorm();
        dto.selectedCalorieLimit = profile.getSelectedCalorieLimit();
        dto.dailyProteinLimit = profile.getDailyProteinLimit();
        dto.dailyFatLimit = profile.getDailyFatLimit();
        dto.dailyCarbsLimit = profile.getDailyCarbsLimit();
        dto.dailyProteinPercentage = profile.getDailyProteinPercentage();
        dto.dailyFatPercentage = profile.getDailyFatPercentage();
        dto.dailyCarbsPercentage = profile.getDailyCarbsPercentage();

        return dto;
    }
}
