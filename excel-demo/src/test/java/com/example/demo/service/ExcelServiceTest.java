//package com.example.demo.service;
//
//import com.example.demo.model.DataList;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.junit.jupiter.api.*;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.io.*;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ExcelServiceTest {
//
//    private ExcelService excelService;
//
//    @BeforeEach
//    void setUp() {
//        excelService = new ExcelService();
//    }
//
//    // ── helpers ───────────────────────────────────────────────────────────────
//
//    /** Build a simple .xlsx file in memory with the given headers + rows */
//    private MockMultipartFile buildExcelFile(List<String> headers,
//                                              List<List<Object>> rows) throws IOException {
//        try (Workbook wb = new XSSFWorkbook();
//             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//
//            Sheet sheet = wb.createSheet("Sheet1");
//
//            // header row
//            Row headerRow = sheet.createRow(0);
//            for (int i = 0; i < headers.size(); i++) {
//                headerRow.createCell(i).setCellValue(headers.get(i));
//            }
//
//            // data rows
//            for (int r = 0; r < rows.size(); r++) {
//                Row dataRow = sheet.createRow(r + 1);
//                List<Object> rowData = rows.get(r);
//                for (int c = 0; c < rowData.size(); c++) {
//                    Cell cell = dataRow.createCell(c);
//                    Object val = rowData.get(c);
//                    if (val instanceof Number n) cell.setCellValue(n.doubleValue());
//                    else                         cell.setCellValue(String.valueOf(val));
//                }
//            }
//
//            wb.write(out);
//            return new MockMultipartFile(
//                    "file", "test.xlsx",
//                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
//                    out.toByteArray());
//        }
//    }
//
//    // ── readExcel ─────────────────────────────────────────────────────────────
//
//    @Test
//    @DisplayName("readExcel: should return correct number of rows and headers")
//    void testReadExcel_basicData() throws IOException {
//        MockMultipartFile file = buildExcelFile(
//                List.of("Name", "Age", "City"),
//                List.of(
//                        List.of("Alice", 30, "Bangkok"),
//                        List.of("Bob",   25, "London")
//                ));
//
//        List<List<String>> data = excelService.readExcel(file);
//
//        assertEquals(3, data.size(),         "Should have 1 header + 2 data rows");
//        assertEquals("Name", data.get(0).get(0));
//        assertEquals("Alice", data.get(1).get(0));
//        assertEquals("25",    data.get(2).get(1)); // integer, not "25.0"
//    }
//
//    @Test
//    @DisplayName("readExcel: whole numbers should NOT have .0 suffix")
//    void testReadExcel_numericFormatting() throws IOException {
//        MockMultipartFile file = buildExcelFile(
//                List.of("Score"),
//                List.of(List.of(100)));
//
//        List<List<String>> data = excelService.readExcel(file);
//        assertEquals("100", data.get(1).get(0), "Integer should be '100' not '100.0'");
//    }
//
//    @Test
//    @DisplayName("readExcel: empty file should throw IllegalArgumentException")
//    void testReadExcel_emptyFile() {
//        MockMultipartFile empty = new MockMultipartFile(
//                "file", "empty.xlsx",
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
//                new byte[0]);
//
//        assertThrows(IllegalArgumentException.class, () -> excelService.readExcel(empty));
//    }
//
//    @Test
//    @DisplayName("readExcel: wrong extension should throw IllegalArgumentException")
//    void testReadExcel_wrongExtension() {
//        MockMultipartFile txt = new MockMultipartFile(
//                "file", "data.txt", "text/plain", "hello".getBytes());
//
//        assertThrows(IllegalArgumentException.class, () -> excelService.readExcel(txt));
//    }
//
//    // ── writeExcel ────────────────────────────────────────────────────────────
//
//    @Test
//    @DisplayName("writeExcel: output stream should not be empty")
//    void testWriteExcel_producesBytes() throws IOException {
//        List<List<String>> data = List.of(
//                List.of("Product", "Price"),
//                List.of("Widget",  "9.99")
//        );
//
//        ByteArrayInputStream result = excelService.writeExcel(data);
//        assertNotNull(result);
//        assertTrue(result.available() > 0);
//    }
//
//    @Test
//    @DisplayName("writeExcel: generated file should be re-readable")
//    void testWriteExcel_roundTrip() throws IOException {
//        List<List<String>> original = List.of(
//                List.of("Col1", "Col2"),
//                List.of("Hello", "World")
//        );
//
//        ByteArrayInputStream stream = excelService.writeExcel(original);
//        byte[] bytes = stream.readAllBytes();
//
//        // Re-open the generated workbook with POI and verify the first cell
//        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
//            Sheet sheet = wb.getSheetAt(0);
//            assertEquals("Col1",  sheet.getRow(0).getCell(0).getStringCellValue());
//            assertEquals("Hello", sheet.getRow(1).getCell(0).getStringCellValue());
//        }
//    }
//
//    // ── writeStyledExcel ──────────────────────────────────────────────────────
//
//    @Test
//    @DisplayName("writeStyledExcel: header row should be bold")
//    void testWriteStyledExcel_headerIsBold() throws IOException {
//        List<List<String>> data = List.of(
//                List.of("Name", "Dept"),
//                List.of("Alice", "Engineering")
//        );
//
//        byte[] bytes = excelService.writeStyledExcel(data).readAllBytes();
//
//        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
//            Cell headerCell = wb.getSheetAt(0).getRow(0).getCell(0);
//            Font font = wb.getFontAt(headerCell.getCellStyle().getFontIndex());
//            assertTrue(font.getBold(), "Header font should be bold");
//        }
//    }
//
//    // ── writeEmployeesToExcel ─────────────────────────────────────────────────
//
//    @Test
//    @DisplayName("writeEmployeesToExcel: should write correct number of data rows")
//    void testWriteEmployeesToExcel() throws IOException {
//        List<DataList> employees = List.of(
//                new DataList(1L, "Alice", "alice@test.com", 50000.0, "IT"),
//                new DataList(2L, "Bob",   "bob@test.com",   60000.0, "HR")
//        );
//
//        byte[] bytes = excelService.writeEmployeesToExcel(employees).readAllBytes();
//
//        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
//            Sheet sheet = wb.getSheetAt(0);
//            assertEquals("ID",    sheet.getRow(0).getCell(0).getStringCellValue());
//            assertEquals("Alice", sheet.getRow(1).getCell(1).getStringCellValue());
//            assertEquals(2,       sheet.getLastRowNum(),
//                    "Should have 2 data rows (plus 1 header = row index 2)");
//        }
//    }
//
//    // ── readEmployeesFromExcel ────────────────────────────────────────────────
//
//    @Test
//    @DisplayName("readEmployeesFromExcel: should parse employees correctly")
//    void testReadEmployeesFromExcel() throws IOException {
//        MockMultipartFile file = buildExcelFile(
//                List.of("ID", "Name", "Email", "Salary", "Department"),
//                List.of(
//                        List.of(1, "Alice", "alice@test.com", 50000, "IT"),
//                        List.of(2, "Bob",   "bob@test.com",   60000, "HR")
//                ));
//
//        List<DataList> employees = excelService.readEmployeesFromExcel(file);
//
//        assertEquals(2, employees.size());
//        assertEquals("Alice", employees.get(0).getName());
//        assertEquals("HR",    employees.get(1).getDepartment());
//        assertEquals(50000.0, employees.get(0).getSalary());
//    }
//
//    // ── validateExcelFile ─────────────────────────────────────────────────────
//
//    @Test
//    @DisplayName("validateExcelFile: valid .xlsx should not throw")
//    void testValidateExcelFile_valid() throws IOException {
//        MockMultipartFile file = buildExcelFile(
//                List.of("A"), List.of(List.of("x")));
//        assertDoesNotThrow(() -> excelService.validateExcelFile(file));
//    }
//
//    @Test
//    @DisplayName("validateExcelFile: null filename should throw")
//    void testValidateExcelFile_nullFilename() {
//        MockMultipartFile file = new MockMultipartFile(
//                "file", null, "application/octet-stream", "data".getBytes());
//        assertThrows(IllegalArgumentException.class,
//                () -> excelService.validateExcelFile(file));
//    }
//
//    // ── getCellValueAsString ──────────────────────────────────────────────────
//
//    @Test
//    @DisplayName("getCellValueAsString: null cell returns empty string")
//    void testGetCellValue_null() {
//        assertEquals("", excelService.getCellValueAsString(null));
//    }
//}
