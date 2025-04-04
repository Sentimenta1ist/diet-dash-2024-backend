package app.dietdash.controller;


import app.dietdash.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartController {

    @GetMapping("/main")
    public String getMainChart(@AuthenticationPrincipal UserDto principal) {
        // Текущая дата (04.04.2025, как указано в контексте)
        LocalDate currentDate = LocalDate.of(2025, 4, 4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Создаем массив для 7 дней
        JSONArray chartData = new JSONArray();
        Random random = new Random();

        // Генерируем данные за 7 дней: 3 дня назад, текущий день, 3 дня вперед
        for (int i = -3; i <= 3; i++) {
            LocalDate date = currentDate.plusDays(i);
            String formattedDate = date.format(formatter);

            // Генерируем случайные значения для "Клиенты" (20-40) и "Диеты" (10-25)
            int clients = 20 + random.nextInt(21); // Число от 20 до 40
            int diets = 10 + random.nextInt(16);   // Число от 10 до 25

            // Создаем объект для каждой даты
            JSONObject dayData = new JSONObject();
            dayData.put("date", formattedDate);
            dayData.put("clients", clients);
            dayData.put("diets", diets);

            chartData.put(dayData);
        }

        // Возвращаем JSON как строку
        return chartData.toString();
    }

}
