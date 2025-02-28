package app.dietdash.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Activity {
    NO_ACTIVITY(0, "Без врахування навантаження (ООВ * 1)", "Without physical activity (BMR * 1)", 1.0),
    SITTING(1, "Сидячий спосіб життя (ООВ * 1.2)", "Sedentary lifestyle (BMR * 1.2)", 1.2),
    EASY(2, "Мала активність (ООВ * 1.375)", "Low activity (BMR * 1.375)", 1.375),
    MEDIUM(3, "Помірна активність (ООВ * 1.55)", "Moderate activity (BMR * 1.55)", 1.55),
    HARD(4, "Висока активність (ООВ * 1.725)", "High activity (BMR * 1.725)", 1.725),
    VERY_HARD(5, "Дуже висока активність (ООВ * 1.9)", "Very high activity (BMR * 1.9)", 1.9);

    final int id;
    final String description;
    final String enDescription;
    final double multiplier;

    Activity(int id, String ua, String en, double coe) {
        this.id = id;
        this.description = ua;
        this.enDescription = en;
        this.multiplier = coe;
    }

    @JsonCreator
    public static Activity fromId(int id) {
        for (Activity activity : values()) {
            if (activity.getId() == id) {
                return activity;
            }
        }
        throw new IllegalArgumentException("Unknown Activity id: " + id);
    }
}
