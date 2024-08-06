package app.dietdash.service;

import app.dietdash.model.Meal;
import app.dietdash.model.Product;
import app.dietdash.repository.MealRepository;
import app.dietdash.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final ProductRepository productRepository;


    public MealService(MealRepository mealRepository, ProductRepository productRepository) {
        this.mealRepository = mealRepository;
        this.productRepository = productRepository;
    }

    public void addMeal(Meal meal) {
        Product product = productRepository.findById(meal.getProduct().getId()).orElse(null);

        meal.setProduct(product);

        mealRepository.save(meal);
    }

    public List<Meal> getMealsByDate(LocalDate date, UUID profileId) {
        return null;
    }

}
