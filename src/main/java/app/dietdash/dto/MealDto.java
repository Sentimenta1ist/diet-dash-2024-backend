package app.dietdash.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MealDto {
    @JsonFormat(pattern = "HH:mm")
    LocalTime time;

    int grams;

    Integer productId;
}
