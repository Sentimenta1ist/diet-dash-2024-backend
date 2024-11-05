package app.dietdash.service;

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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ExportService {

    private final RationService rationService;

    public byte[] exportDoc(final LocalDate start, final LocalDate end, final UUID profileId) {
        if (start.isAfter(end)) {
            throw new AppException("startDate should be before or equal endDate", HttpStatus.BAD_REQUEST);
        }

        LocalDate date = start;
        // Create a new Word document
//        XWPFDocument document = new XWPFDocument();
//        List<Ration> rations = new ArrayList<>();
//        rations.add(createMockRation1());
//        rations.add(createMockRation2());
//
//        for (Ration ration : rations) {
//
//            // Add a table title
//            XWPFParagraph title = document.createParagraph();
//            XWPFRun titleRun = title.createRun();
//            String dayWithDate = uaTransform(ration.getDate().getDayOfWeek()) + " (" + ration.getDate().format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
//            titleRun.setText(dayWithDate);
//            titleRun.setBold(true);
//            titleRun.setFontSize(12);
//            title.setAlignment(ParagraphAlignment.LEFT);
//
//            // Create a table with 3 columns
//            XWPFTable table = document.createTable();
//
//            // Set custom width for the table
//            CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
//            tblWidth.setW(BigInteger.valueOf(8500));
//            tblWidth.setType(STTblWidth.DXA);
//
//            // Add header row
//            XWPFTableRow headerRow = table.getRow(0); // The first row is created by default
//            formatHeaderCell(headerRow.getCell(0), "Час", "60d0fc", "000000"); // Time
//            formatHeaderCell(headerRow.addNewTableCell(), "Назва продукту", "F4B084", "000000"); // Product Name
//            formatHeaderCell(headerRow.addNewTableCell(), "Порція(г)", "A9D08E", "000000"); // Portion (g)
//
//
//            for(Meal meal : ration.getMeals()) {
//                addBlankRowWithMergedCells(table);
//                addTableRow(table, meal);
//            }
//        }
//
//
//        // Save the document to a file
//        try (FileOutputStream out = new FileOutputStream("DietPlan.docx")) {
//            document.write(out);
//        }
//
//        System.out.println("Word document created successfully.");
        return null;
    }

    private static void formatHeaderCell(XWPFTableCell cell, String text, String bgColor, String textColor) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setColor(textColor);
        run.setFontSize(12);

        cell.setColor(bgColor); // Set background color
    }

    private static void addTableRow(XWPFTable table, Meal meal) {
        XWPFTableRow row = table.createRow();
        setTableCellText(row.getCell(0), meal.getTime().toString(), ParagraphAlignment.CENTER);
        setTableCellText(row.getCell(1), " " + meal.getProduct().getName(), ParagraphAlignment.LEFT);
        setTableCellText(row.getCell(2), "" + meal.getGrams(), ParagraphAlignment.CENTER);
    }

    private static void setTableCellText(XWPFTableCell cell, String text, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(alignment);

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(11);
    }

    // Insert this function to add a blank row with merged cells
    private static void addBlankRowWithMergedCells(XWPFTable table) {
        XWPFTableRow blankRow = table.createRow();

        // Create the first cell and set it to span across other cells
        XWPFTableCell cell = blankRow.getCell(0);
        cell.setText(""); // Leave text empty for spacing

        // Merge the cells in this row across all three columns
        mergeCellsHorizontal(blankRow, 0, 2);
    }

    // Helper function to merge cells horizontally
    private static void mergeCellsHorizontal(XWPFTableRow row, int startCol, int endCol) {
        for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
            XWPFTableCell cell = row.getCell(colIndex);
            if (colIndex == startCol) {
                // Set the first cell in the merge region to start the merge
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                // Set other cells to continue the merge
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
}
