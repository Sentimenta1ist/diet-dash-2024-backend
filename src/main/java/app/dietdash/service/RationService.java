package app.dietdash.service;

import app.dietdash.dto.MealDto;
import app.dietdash.model.Meal;
import app.dietdash.model.Product;
import app.dietdash.model.Profile;
import app.dietdash.model.Ration;
import app.dietdash.repository.MealRepository;
import app.dietdash.repository.ProductRepository;
import app.dietdash.repository.ProfileRepository;
import app.dietdash.repository.RationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RationService {

    private final RationRepository rationRepository;
    private final ProfileRepository profileRepository;
    private final MealRepository mealRepository;
    private final ProductRepository productRepository;

    public String addMealToRation(MealDto meal, UUID profileId, LocalDate date) {

        final Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isEmpty()) {
            return "Profile does not exist";
        }

        final Optional<Product> product = productRepository.findById(meal.getProductId());
        if (product.isEmpty()) {
            return "Product with id {" + meal.getProductId() + "} does not exist";
        }

        final Meal mealEntity = Meal.builder()
                .amount(meal.getAmount())
                .product(product.get())
                .time(meal.getTime())
                .build();

        mealRepository.save(mealEntity);

        final Optional<Ration> ration = rationRepository.findByProfileAndDate(profile.get(), date);

        if (ration.isEmpty()) {
            final Ration newRation = Ration.builder()
                    .meals(List.of(mealEntity))
                    .date(date)
                    .profile(profile.get())
                    .build();

            rationRepository.save(newRation);
        } else {
            ration.get().getMeals().add(mealEntity);
            rationRepository.save(ration.get());
        }

        return "Saved.";
    }

    public Ration getRationByProfileAndDate(final UUID profileId, final LocalDate date) {
        return profileRepository.findById(profileId)
                .flatMap(value -> rationRepository.findByProfileAndDate(value, date))
                .orElse(null);
    }

    public void addMeal(MealDto mealDto) {

        final Optional<Product> product = productRepository.findById(mealDto.getProductId());
        if (product.isEmpty()) {
            return;
        }

        final Meal mealEntity = Meal.builder()
                .amount(mealDto.getAmount())
                .product(product.get())
                .time(mealDto.getTime())
                .build();

        mealRepository.save(mealEntity);
    }

}
