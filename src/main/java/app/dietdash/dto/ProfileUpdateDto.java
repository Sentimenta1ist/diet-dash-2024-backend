package app.dietdash.dto;

import app.dietdash.enums.Activity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDto {
    private String name;
    private Integer age;
    private String gender;
    private Integer height;
    private Double weight;
    private Integer waist;
    private Integer hips;
    private Integer goalWeight;
    private Activity activity;
    private Double bodyMassIndex;
    private Integer dailyCalorieNorm;
    private Integer selectedCalorieLimit;
    private Integer dailyProteinLimit;
    private Integer dailyFatLimit;
    private Integer dailyCarbsLimit;
    private Double dailyProteinPercentage;
    private Double dailyFatPercentage;
    private Double dailyCarbsPercentage;
}