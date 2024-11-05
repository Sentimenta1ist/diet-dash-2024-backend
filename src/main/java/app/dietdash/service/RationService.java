package app.dietdash.service;

import app.dietdash.dto.MealDto;
import app.dietdash.model.Meal;
import app.dietdash.model.Product;
import app.dietdash.model.Profile;
import app.dietdash.model.Ration;
import app.dietdash.repository.MealRepository;
import app.dietdash.repository.ProductRepository;
import app.dietdash.repository.RationRepository;
import exceptions.AppException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RationService {

    private final RationRepository rationRepository;
    private final ProfileService profileService;
    private final MealRepository mealRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addMealToRation(MealDto meal, UUID profileId, LocalDate date) {

        Profile profile = profileService.getProfileById(profileId);

        final Product product = productRepository.findById(meal.getProductId())
                .orElseThrow(() -> new AppException("Unknown profile with id: " + meal.getProductId(), HttpStatus.NOT_FOUND));

        final Ration ration = rationRepository.findByProfileAndDate(profile, date)
                .orElse(Ration.builder()
                        .meals(List.of())
                        .date(date)
                        .profile(profile)
                        .build());

        rationRepository.save(ration);

        final Meal mealEntity = Meal.builder()
                .grams(meal.getGrams())
                .product(product)
                .time(meal.getTime())
                .ration(ration)
                .build();

        mealRepository.save(mealEntity);
        if (ration.getMeals() == null) {
            ration.setMeals(List.of(mealEntity));
        }
    }

    public Ration getRationByProfileAndDate(final UUID profileId, final LocalDate date) {
        return profileService.getProfileById(profileId)
                .getRations().stream()
                .filter(value -> date.isEqual(value.getDate()))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void addMeal(MealDto mealDto) {

        final Optional<Product> product = productRepository.findById(mealDto.getProductId());
        if (product.isEmpty()) {
            return;
        }

        final Meal mealEntity = Meal.builder()
                .grams(mealDto.getGrams())
                .product(product.get())
                .time(mealDto.getTime())
                .build();

        mealRepository.save(mealEntity);
    }

    @Transactional
    public void deleteMeal(final Integer mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new AppException("Unknown meal with id: " + mealId, HttpStatus.NOT_FOUND));
        mealRepository.delete(meal);
    }

    @Transactional
    public void updateMeal(final Integer mealId, final MealDto updateMeal) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new AppException("Unknown profile with id: " + mealId, HttpStatus.NOT_FOUND));

        meal.setGrams(updateMeal.getGrams());
        meal.setTime(updateMeal.getTime());

        mealRepository.save(meal);
    }

    @Transactional
    public void duplicateRationToDate(UUID profileId, LocalDate sourceDate, LocalDate targetDate) {
        if (sourceDate.isEqual(targetDate)) {
            throw new AppException("Source date and target date cannot be the same.", HttpStatus.BAD_REQUEST);
        }

        Profile profile = profileService.getProfileById(profileId);

        Ration sourceRation = rationRepository.findByProfileAndDate(profile, sourceDate)
                .orElseThrow(() -> new AppException("No ration found for the given date.", HttpStatus.NOT_FOUND));

        Ration targetRation = rationRepository.findByProfileAndDate(profile, targetDate)
                .orElse(Ration.builder()
                        .profile(profile)
                        .date(targetDate)
                        .build());

        // Clear all meals in the target ration before duplicating
        List<Meal> existingMeals = new ArrayList<>(targetRation.getMeals());
        mealRepository.deleteAll(existingMeals); // Delete each meal
        targetRation.getMeals().clear(); // Clear the in-memory collection
        rationRepository.save(targetRation);

        sourceRation.getMeals().forEach(meal -> {
            Meal duplicatedMeal = Meal.builder()
                    .grams(meal.getGrams())
                    .product(meal.getProduct())
                    .time(meal.getTime())
                    .ration(targetRation)
                    .build();
            mealRepository.save(duplicatedMeal);
        });
    }
}
