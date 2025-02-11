package app.dietdash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileDto {
    private UUID id;
    private String name;
    private int age;
    private String gender;
    private int height;
    private double weight;
    private int selectedCalorieLimit;
}
