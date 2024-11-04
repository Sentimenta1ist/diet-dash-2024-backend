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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
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
                        .meals(List.of())
                        .build());

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

    public byte[] exportXlsx(final LocalDate start, final LocalDate end, final UUID profileId) {

        if (start.isAfter(end)) {
            throw new AppException("startDate should be before or equal endDate", HttpStatus.BAD_REQUEST);
        }

        LocalDate date = start;

        try (Workbook workbook = new XSSFWorkbook()) {

            // Write the output to a byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Sheet sheet = workbook.createSheet("Diet plan");

            // Set the column widths
            sheet.setColumnWidth(0, 4000); // Set width for "Meal Time"
            sheet.setColumnWidth(1, 14000); // Set width for "Product Name"
            sheet.setColumnWidth(2, 4000); // Set width for "Grams"

            // Create header row
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.setHeightInPoints(22);

            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("Час");
            headerCell1.setCellStyle(createCellStyle(workbook, IndexedColors.PALE_BLUE, HorizontalAlignment.CENTER, true));


            Cell headerCell3 = headerRow.createCell(1);
            headerCell3.setCellValue("Назва продукту");
            headerCell3.setCellStyle(createCellStyle(workbook, IndexedColors.TAN, HorizontalAlignment.CENTER, true));

            Cell headerCell2 = headerRow.createCell(2);
            headerCell2.setCellValue("Порція(г)");
            headerCell2.setCellStyle(createCellStyle(workbook, IndexedColors.LIGHT_GREEN, HorizontalAlignment.CENTER, true));

            while (!date.isAfter(end)) {
                // Merge cells to display the day of the week with the date
                Row dateRow = sheet.createRow(rowNum++);
                // Merge across the first three columns for the day
                Cell dateCell = dateRow.createCell(0);
                Cell date2Cell = dateRow.createCell(1);
                Cell date3Cell = dateRow.createCell(2);
                String dayWithDate = uaTransform(date.getDayOfWeek()) + " (" + date.format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
                dateCell.setCellValue(dayWithDate);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
                dateCell.setCellStyle(createCellStyle(workbook, IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER, false));
                date2Cell.setCellStyle(createCellStyle(workbook, IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER, false));
                date3Cell.setCellStyle(createCellStyle(workbook, IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER, false));

                Ration ration = getRationByProfileAndDate(profileId, date);
                if (ration != null) {

                    List<Meal> sortedMeals = ration.getMeals().stream()
                            .sorted(Comparator.comparing(Meal::getTime))
                            .toList();

                    for (int i = 0; i < sortedMeals.size(); i++) {
                        Meal meal = sortedMeals.get(i);
                        if (i > 0 && !sortedMeals.get(i - 1).getTime().equals(meal.getTime())) {
                            Row row = sheet.createRow(rowNum++);
                            row.setHeightInPoints(15);
                        }
                        Row row = sheet.createRow(rowNum++);
                        row.setHeightInPoints(22);

                        row.createCell(0).setCellValue(meal.getTime().toString() + " ");
                        row.createCell(1).setCellValue(" " + meal.getProduct().getName());
                        row.createCell(2).setCellValue(meal.getGrams());

                        row.getCell(0).setCellStyle(createCellStyle(workbook, IndexedColors.WHITE, HorizontalAlignment.RIGHT, false));
                        row.getCell(1).setCellStyle(createCellStyle(workbook, IndexedColors.WHITE, HorizontalAlignment.LEFT, false));
                        row.getCell(2).setCellStyle(createCellStyle(workbook, IndexedColors.WHITE, HorizontalAlignment.LEFT, false));

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

    private CellStyle createCellStyle(Workbook workbook, IndexedColors backgroundColor, HorizontalAlignment alignment, boolean bold) {
        CellStyle style = workbook.createCellStyle();

        // Set font style
        Font font = workbook.createFont();
        font.setBold(bold);
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.GREY_80_PERCENT.getIndex()); // Set header font color to white
        style.setFont(font);

        // Set background color and fill pattern
        style.setFillForegroundColor(backgroundColor.getIndex()); // Custom background color
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(alignment);

        // Add borders to the cell
        style.setBorderTop(BorderStyle.THIN);   // Top border
        style.setBorderBottom(BorderStyle.THIN); // Bottom border
        style.setBorderLeft(BorderStyle.THIN);   // Left border
        style.setBorderRight(BorderStyle.THIN);  // Right border

        // Set border color (optional, you can use a different color)
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());


        return style;
    }

    private String uaTransform(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Понеділок";
            case TUESDAY -> "Вівторок";
            case WEDNESDAY -> "Середа";
            case THURSDAY -> "Четвер";
            case FRIDAY -> "Пʼятниця";
            case SATURDAY -> "Субота";
            case SUNDAY -> "Неділя";
        };
    }
}
