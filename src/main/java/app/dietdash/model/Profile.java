package app.dietdash.model;

import app.dietdash.enums.Activity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;

    private String name;
    private int age;
    private String gender;
    private int height;
    private double weight;
    private int waist;
    private int hips;
    private int goalWeight;
    private Activity activity = Activity.NO_ACTIVITY;
    private double bodyMassIndex;
    private int dailyCalorieNorm;
    private int selectedCalorieLimit;
    private int dailyProteinLimit;
    private int dailyFatLimit;
    private int dailyCarbsLimit;

    @OneToMany(mappedBy = "profile")
    @JsonIgnore
    private List<Ration> rations = Collections.emptyList();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

}
