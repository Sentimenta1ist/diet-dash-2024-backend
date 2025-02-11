package app.dietdash.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RationDto {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private List<MealWithNameDto> meals = new ArrayList<>();
}
