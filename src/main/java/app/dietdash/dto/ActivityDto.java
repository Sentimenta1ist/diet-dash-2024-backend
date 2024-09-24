package app.dietdash.dto;

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
}
