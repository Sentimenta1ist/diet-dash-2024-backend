package app.dietdash.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    private int limitCal;
    private int carbs;
    private int proteins;
    private int fats;
    private int activity;

    @OneToMany(mappedBy = "profile")
    private List<Ration> rations;

    public Profile() {
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", talia=" + talia +
                ", bedra=" + bedra +
                ", weightAim=" + weightAim +
                ", aim='" + aim + '\'' +
                ", limitCal=" + limitCal +
                ", carbs=" + carbs +
                ", proteins=" + proteins +
                ", fats=" + fats +
                ", activity=" + activity +
                ", rations=" + rations +
                '}';
    }
}
