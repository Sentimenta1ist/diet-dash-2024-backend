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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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

    public String addMealToRation(MealDto meal, UUID profileId, LocalDate date) {

        Profile profile = profileService.getProfileById(profileId);

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

        final Optional<Ration> ration = rationRepository.findByProfileAndDate(profile, date);

        if (ration.isEmpty()) {
            final Ration newRation = Ration.builder()
                    .meals(List.of(mealEntity))
                    .date(date)
                    .profile(profile)
                    .build();

            rationRepository.save(newRation);
        } else {
            ration.get().getMeals().add(mealEntity);
            rationRepository.save(ration.get());
        }

        return "Saved.";
    }

    public Ration getRationByProfileAndDate(final UUID profileId, final LocalDate date) {
        return profileService.getProfileById(profileId)
                .getRations().stream()
                .filter(value -> date.isEqual(value.getDate()))
                .findFirst()
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

    public byte[] exportXlsx(final LocalDate start, final LocalDate end, final UUID profileId) {

        if (start.isAfter(end)) {
            throw new AppException("startDate should be before or equal endDate", HttpStatus.BAD_REQUEST);
        }

        LocalDate date = start;

        try (Workbook workbook = new XSSFWorkbook()) {

            // Write the output to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            while (!date.isAfter(end)) {
                // Create a sheet
                Sheet sheet = workbook.createSheet(date.format(DateTimeFormatter.ISO_DATE));

                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Meal Time");
                headerRow.createCell(1).setCellValue("Product Name");
                headerRow.createCell(2).setCellValue("Amount (grams)");


                Ration ration = getRationByProfileAndDate(profileId, date);
                if (ration != null) {
                    int rowNum = 1;
                    List<Meal> sortedMeals = ration.getMeals().stream()
                            .sorted(Comparator.comparing(Meal::getTime))
                            .toList();
                    for (Meal meal : sortedMeals) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(meal.getTime().toString());
                        row.createCell(1).setCellValue(meal.getProduct().getName());
                        row.createCell(2).setCellValue(meal.getAmount());
                    }
                }
                date = date.plusDays(1);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
