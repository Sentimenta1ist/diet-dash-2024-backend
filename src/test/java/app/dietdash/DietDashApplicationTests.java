package app.dietdash;

import app.dietdash.model.Meal;
import app.dietdash.model.Product;
import app.dietdash.model.Ration;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.IOException;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class DietDashApplicationTests {

    @Test
    public void test() throws IOException {

        // Create a new Word document
        XWPFDocument document = new XWPFDocument();
        List<Ration> rations = new ArrayList<>();
        rations.add(createMockRation1());
        rations.add(createMockRation2());

        // Create a table with 3 columns
        XWPFTable table = document.createTable();

        // Disable Autofit for the table
        CTTbl tableCT = table.getCTTbl();
        CTTblPr tblPr = tableCT.getTblPr() == null ? tableCT.addNewTblPr() : tableCT.getTblPr();
        tblPr.addNewTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(10000));

        // Add header row
        XWPFTableRow headerRow = table.getRow(0); // The first row is created by default
        formatHeaderCell(headerRow.getCell(0), "Час", "afcefa", "000000"); // Time
        formatHeaderCell(headerRow.addNewTableCell(), "    Назва продукту", "ffd194", "000000"); // Product Name
        formatHeaderCell(headerRow.addNewTableCell(), "Порція(г)", "b2f7c5", "000000"); // Portion (g)
        setRowMinHeight(headerRow, 400);
        setColumnWidth(headerRow.getCell(0), 1000);
        setColumnWidth(headerRow.getCell(1), 7500);
        setColumnWidth(headerRow.getCell(2), 1500);

        for (Ration ration : rations) {
            if (ration != null) {
                String dayWithDate = uaTransform(ration.getDate().getDayOfWeek()) + " (" + ration.getDate().format(DateTimeFormatter.ofPattern("dd/MM")) + ")";
                addMergedCells(table, dayWithDate, "c2c2c2", 400); // Date

                List<Meal> sortedMeals = ration.getMeals().stream()
                        .sorted(Comparator.comparing(Meal::getTime))
                        .toList();

                List<Meal> meals = ration.getMeals();
                for (int i = 0; i < meals.size(); i++) {
                    Meal meal = sortedMeals.get(i);
                    if (i > 0 && !sortedMeals.get(i - 1).getTime().equals(meal.getTime())) {
                        addMergedCells(table, "", null, 200); // Space
                    }
                    addTableRow(table, meal);
                }
            }
            document.createParagraph();
        }


        // Save the document to a file
//        try (FileOutputStream out = new FileOutputStream("DietPlan.docx")) {
//            document.write(out);
//        }

        System.out.println("Word document created successfully.");
    }

    @Test
    public void test2() throws IOException {

        // Create a new Word document
        XWPFDocument document = new XWPFDocument();
        // Create a table with 3 columns
        XWPFTable table = document.createTable();
        // Disable Autofit for the table
        CTTbl tableCT = table.getCTTbl();
        CTTblPr tblPr = tableCT.getTblPr() == null ? tableCT.addNewTblPr() : tableCT.getTblPr();
        tblPr.addNewTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(BigInteger.valueOf(10000));

        // Add header row
        XWPFTableRow headerRow = table.getRow(0); // The first row is created by default
        formatHeaderCell(headerRow.getCell(0), "Час", "afcefa", "000000"); // Time
        formatHeaderCell(headerRow.addNewTableCell(), "Назва продукту", "ffd194", "000000"); // Product Name
        formatHeaderCell(headerRow.addNewTableCell(), "Порція(г)", "b2f7c5", "000000"); // Portion (g)

        setRowMinHeight(headerRow, 400);

        setColumnWidth(headerRow.getCell(0), 1000);
        setColumnWidth(headerRow.getCell(1), 8000);
        setColumnWidth(headerRow.getCell(2), 1000);

//        try (FileOutputStream out = new FileOutputStream("DietPlan2.docx")) {
//            document.write(out);
//        }
    }


    public static void setRowMinHeight(XWPFTableRow row, int height) {
        CTTrPr trPr = row.getCtRow().addNewTrPr();
        trPr.addNewTrHeight().setVal(BigInteger.valueOf(height));
        trPr.getTrHeightArray(0).setHRule(STHeightRule.EXACT);
    }

    private static void setColumnWidth(XWPFTableCell cell, int width) {
        CTTcPr tcPr = cell.getCTTc().addNewTcPr();
        CTTblWidth cellWidth = tcPr.addNewTcW();
        cellWidth.setW(BigInteger.valueOf(width));
    }

    private static void formatHeaderCell(XWPFTableCell cell, String text, String bgColor, String textColor) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);


        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setColor(textColor);
        run.setFontSize(15);

        cell.setColor(bgColor); // Set background color
    }

    private static void addTableRow(XWPFTable table, Meal meal) {
        XWPFTableRow row = table.createRow();

        setRowMinHeight(row, 400);

        // Disable automatic row height adjustment to enforce the specified height
        setTableCellText(row.getCell(0), meal.getTime().toString(), ParagraphAlignment.CENTER);
        setTableCellText(row.getCell(1), " " + meal.getProduct().getName(), ParagraphAlignment.LEFT);
        setTableCellText(row.getCell(2), "" + meal.getGrams(), ParagraphAlignment.CENTER);

    }

    private static void setTableCellText(XWPFTableCell cell, String text, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
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

        XWPFParagraph paragraph = cell.getParagraphs().get(0);
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

    private Ration createMockRation1() {
        Ration ration = new Ration();
        List<Meal> meals = new ArrayList<>();

        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Toмат"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("ТоматоБанан"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("ТоматоБанан"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("ТоматоБанан"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("ТоматоБанан"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Банан"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));

        ration.setMeals(meals);
        ration.setDate(LocalDate.of(2024, 5, 6));
        return ration;
    }

    private Ration createMockRation2() {
        Ration ration = new Ration();
        List<Meal> meals = new ArrayList<>();

        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Toматна паста"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Toматна паста"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Toматна паста"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Toматна паста"), ration));
        meals.add(new Meal(0L, LocalTime.of(12, 10), 100, createMockProduct("Toматна паста"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Бананове пюре"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Бананове пюре"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Бананове пюре"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Бананове пюре"), ration));
        meals.add(new Meal(0L, LocalTime.of(13, 15), 150, createMockProduct("Бананове пюре"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));
        meals.add(new Meal(0L, LocalTime.of(20, 30), 160, createMockProduct("Лапша зі смаком"), ration));

        ration.setMeals(meals);
        ration.setDate(LocalDate.of(2024, 5, 7));
        return ration;
    }


    private Product createMockProduct(String name) {
        return new Product(0L, name, 0.0f, 0.0f, 0.0f, 0.0f, null);
    }
}

