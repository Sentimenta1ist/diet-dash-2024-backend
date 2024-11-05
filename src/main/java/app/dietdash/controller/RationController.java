package app.dietdash.controller;

import app.dietdash.dto.LocalDatesDto;
import app.dietdash.dto.MealDto;
import app.dietdash.model.Ration;
import app.dietdash.service.ExportService;
import app.dietdash.service.RationService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/ration")
@AllArgsConstructor
public class RationController {

    private final RationService rationService;
    private final ExportService exportService;

    @GetMapping("/{profileId}")
    public ResponseEntity<Ration> getRation(
            @PathVariable UUID profileId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(rationService.getRationByProfileAndDate(profileId, date));
    }

    @PostMapping("/add/{profileId}")
    public ResponseEntity<String> addMealToRation(
            @PathVariable UUID profileId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody MealDto meal) {
        rationService.addMealToRation(meal, profileId, date);
        return ResponseEntity.ok("Added.");
    }

    @DeleteMapping("/delete/{mealId}")
    public ResponseEntity<String> deleteMeal(
            @PathVariable Integer mealId) {
        rationService.deleteMeal(mealId);
        return ResponseEntity.ok("Deleted");
    }

    @PutMapping("/update/{mealId}")
    public ResponseEntity<String> updateMeal(
            @PathVariable Integer mealId,
            @RequestBody MealDto meal) {

        rationService.updateMeal(mealId, meal);
        return ResponseEntity.ok("Updated");
    }


    @PostMapping("/add")
    public ResponseEntity<String> addMeal(@RequestBody MealDto meal) {
        rationService.addMeal(meal);
        return ResponseEntity.ok("Added");
    }

    @PostMapping("/duplicate/{profileId}")
    public ResponseEntity<String> duplicateRation(
            @PathVariable UUID profileId,
            @RequestParam("sourceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sourceDate,
            @RequestBody LocalDatesDto localDatesDto) {
        rationService.duplicateRationToDate(profileId, sourceDate, localDatesDto.getTargetDates());
        return ResponseEntity.ok("Duplicated.");
    }

    @GetMapping("/exportXlsx")
    public ResponseEntity<byte[]> exportRationXlsx(
            @RequestParam("profileId") UUID profileId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        // Create HTTP response with Excel content
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rations.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return new ResponseEntity<>(exportService.exportXlsx(start, end, profileId), headers, HttpStatus.OK);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportRationDoc(
            @RequestParam("profileId") UUID profileId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        // Create HTTP response with Excel content
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Rations.docx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        return new ResponseEntity<>(exportService.exportDoc(start, end, profileId), headers, HttpStatus.OK);
    }

}
