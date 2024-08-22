package app.dietdash.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Activity {
    NO_ACTIVITY(0, "Без активності (ООВ * 1)", 1.0),
    ACTIVE(1, "Помірна активність (ООВ * 1.55)", 1.55),
    VERY_ACTIVE(2, "Висока активність (ООВ * 2)", 2);

    final int id;
    final String description;

    @JsonIgnore
    final double coe;

    Activity(int id, String description, double coe) {
        this.id = id;
        this.description = description;
        this.coe = coe;
    }

    @JsonValue
    public String getDescription() {
        return description;
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
