package app.dietdash.model;

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
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private int age;
    private String sex;
    private int height;
    private int weight;
    private int talia;
    private int bedra;
    private int weightAim;
    private String aim;
    private int selectedLimitCal;
    private int selectedCarbs;
    private int selectedProteins;
    private int selectedFats;
    private int recomendedLimitCal;
    private int recomendedCarbs;
    private int recomendedProteins;
    private int recomendedFats;
    private int activity;

    @OneToMany(mappedBy = "profile")
    private List<Ration> rations;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Profile() {
        rations = Collections.emptyList();
    }
}
