package app.dietdash.service;

import app.dietdash.dto.MealWithNameDto;
import app.dietdash.dto.RationDto;
import app.dietdash.model.Meal;
import app.dietdash.model.Ration;
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
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ExportService {

    private final RationService rationService;

    public byte[] exportDoc(final String lang, final LocalDate start, final LocalDate end, final UUID profileId) {
        if (start.isAfter(end)) {
            throw new AppException("startDate should be before or equal endDate", HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDate date = start;
            // Create a new Word document
            XWPFDocument document = new XWPFDocument();
            XWPFTable table = document.createTable();

            // Disable Autofit for the table
            CTTbl tableCT = table.getCTTbl();
            CTTblPr tblPr = tableCT.getTblPr() == null ? tableCT.addNewTblPr() : tableCT.getTblPr();
            tblPr.addNewTblW().setType(STTblWidth.DXA);
            tblPr.getTblW().setW(10000L);

            // Add header row
            XWPFTableRow headerRow = table.getRow(0); // The first row is created by default

            final String timeHeader = "uk".equals(lang) ? "Час" : "Time";
            final String nameHeader = "uk".equals(lang) ? "    Назва продукту" : "    Product name";
            final String sizeHeader = "uk".equals(lang) ? "Порція(г)" : "Portion(g)";

            formatHeaderCell(headerRow.getCell(0), timeHeader, "afcefa", "000000"); // Time
            formatHeaderCell(headerRow.addNewTableCell(), nameHeader, "ffd194", "000000"); // Product Name
            formatHeaderCell(headerRow.addNewTableCell(), sizeHeader, "b2f7c5", "000000"); // Portion (g)
            setRowMinHeight(headerRow, 400);
            setColumnWidth(headerRow.getCell(0), 1000);
            setColumnWidth(headerRow.getCell(1), 7500);
            setColumnWidth(headerRow.getCell(2), 1500);

            while (!date.isAfter(end)) {
                List<RationDto> rations = getRationsForCurrentProfile(lang, date, date, profileId);
                if (!rations.isEmpty() && rations.getFirst() != null) {
                    RationDto ration = rations.getFirst();
                    DayOfWeek dayOfWeek = ration.getDate().getDayOfWeek();
                    String weekday = "uk".equals(lang) ? uaTransform(dayOfWeek) : enTransform(dayOfWeek);
                    String dayWithDate = weekday + " (" + ration.getDate().format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
                    addMergedCells(table, dayWithDate, "c2c2c2", 400); // Date

                    List<MealWithNameDto> sortedMeals = ration.getMeals().stream()
                            .sorted(Comparator.comparing(MealWithNameDto::getTime))
                            .toList();

                    List<MealWithNameDto> meals = ration.getMeals();
                    for (int i = 0; i < meals.size(); i++) {
                        MealWithNameDto meal = sortedMeals.get(i);
                        if (i > 0 && !sortedMeals.get(i - 1).getTime().equals(meal.getTime())) {
                            addMergedCells(table, "", null, 200); // Space
                        }
                        addTableRow(table, meal);
                    }
                }
                date = date.plusDays(1);
            }


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new AppException("Error during the export.", HttpStatus.BAD_REQUEST);
        }
    }


    public static void setRowMinHeight(XWPFTableRow row, int height) {
        CTTrPr trPr = row.getCtRow().addNewTrPr();
        trPr.addNewTrHeight().setVal((long) height);
        trPr.getTrHeightArray(0).setHRule(STHeightRule.EXACT);
    }

    private static void setColumnWidth(XWPFTableCell cell, int width) {
        CTTcPr tcPr = cell.getCTTc().addNewTcPr();
        CTTblWidth cellWidth = tcPr.addNewTcW();
        cellWidth.setW((long) width);
    }

    private static void formatHeaderCell(XWPFTableCell cell, String text, String bgColor, String textColor) {
        XWPFParagraph paragraph = cell.getParagraphs().getFirst();
        paragraph.setAlignment(ParagraphAlignment.CENTER);


        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setColor(textColor);
        run.setFontSize(15);

        cell.setColor(bgColor); // Set background color
    }

    private static void addTableRow(XWPFTable table, MealWithNameDto meal) {
        XWPFTableRow row = table.createRow();

        setRowMinHeight(row, 400);

        // Disable automatic row height adjustment to enforce the specified height
        setTableCellText(row.getCell(0), meal.getTime().toString(), ParagraphAlignment.CENTER);
        setTableCellText(row.getCell(1), " " + meal.getProductName(), ParagraphAlignment.LEFT);
        setTableCellText(row.getCell(2), "" + meal.getGrams(), ParagraphAlignment.CENTER);

    }

    private static void setTableCellText(XWPFTableCell cell, String text, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = cell.getParagraphs().getFirst();
        paragraph.setAlignment(alignment);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(15);
    }

    // Insert this function to add a blank row with merged cells
    private static void addMergedCells(XWPFTable table, String text, String bgColor, int height) {
        XWPFTableRow row = table.createRow();

        setRowMinHeight(row, height);

        XWPFTableCell cell = row.getCell(0);

        XWPFParagraph paragraph = cell.getParagraphs().getFirst();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(15);
        run.setItalic(true);

        if (bgColor != null) {
            cell.setColor(bgColor);
        }

        mergeCellsHorizontal(row, 0, 2);
    }

    // Helper function to merge cells horizontally
    private static void mergeCellsHorizontal(XWPFTableRow row, int startCol, int endCol) {
        for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
            XWPFTableCell cell = row.getCell(colIndex);
            if (colIndex == startCol) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
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

                Ration ration = rationService.getRationByProfileAndDate(profileId, date);
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

    private String enTransform(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Mon";
            case TUESDAY -> "Tue";
            case WEDNESDAY -> "Wed";
            case THURSDAY -> "Thu";
            case FRIDAY -> "Fri";
            case SATURDAY -> "Sat";
            case SUNDAY -> "Sun";
        };
    }

    public List<RationDto> getRationsForCurrentProfile(final String lang, final LocalDate start, final LocalDate end, final UUID profileId) {
        if (start.isAfter(end)) {
            throw new AppException("startDate should be before or equal to endDate", HttpStatus.BAD_REQUEST);
        }

        return start.datesUntil(end.plusDays(1)) // Generate a stream of dates from start to end (inclusive)
                .map(date -> buildRationDtoForDate(profileId, date, lang))
                .filter(Objects::nonNull) // Filter out nulls if no rations exist for that date
                .toList();
    }

    public RationDto getRationForCurrentProfile(final String lang, final LocalDate date, final UUID profileID) {
        List<RationDto> rations = getRationsForCurrentProfile(lang, date, date, profileID);
        if (rations.isEmpty() || rations.getFirst() == null) {
            return null;
        } else {
            return rations.getFirst();
        }
    }

    private RationDto buildRationDtoForDate(UUID profileId, LocalDate date, String lang) {
        Ration ration = rationService.getRationByProfileAndDate(profileId, date);
        if (ration == null || ration.getMeals().isEmpty()) {
            return null;
        }

        List<MealWithNameDto> sortedMealDtos = ration.getMeals().stream()
                .sorted(Comparator.comparing(Meal::getTime))
                .map((Meal meal) -> convertToMealWithNameDto(meal, lang))
                .toList();

        return sortedMealDtos.isEmpty() ? null : new RationDto(ration.getId(), date, sortedMealDtos);
    }

    private MealWithNameDto convertToMealWithNameDto(Meal meal, String lang) {
        if ("uk".equals(lang)) {
            return new MealWithNameDto(meal.getId(), meal.getTime(), meal.getGrams(), meal.getProduct().getName(), meal.getProduct());
        } else {
            return new MealWithNameDto(meal.getId(), meal.getTime(), meal.getGrams(), meal.getProduct().getEngName(), meal.getProduct());
        }
    }
}
