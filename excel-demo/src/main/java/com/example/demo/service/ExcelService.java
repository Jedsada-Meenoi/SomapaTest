package com.example.demo.service;

import com.example.demo.model.DataList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.text.SimpleDateFormat;

import java.io.*;
import java.util.*;


@Service
public class ExcelService {

	public List<DataList> parseAndValidate(MultipartFile file) throws Exception {
        List<DataList> passengers = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        InputStream is = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);

        // Start from row 1 (skip header row 0)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String firstName   = getCellValue(row.getCell(0));
            String lastName    = getCellValue(row.getCell(1));
            String gender      = getCellValue(row.getCell(2));
            String dateOfBirth = getCellValue(row.getCell(3));
            String nationality = getCellValue(row.getCell(4));

            int rowNum = i + 1; 
            List<String> invalidFields = new ArrayList<>();
            if (!firstName.matches("[A-Za-z]+") || firstName.length() > 20) {
                invalidFields.add("First name");
            }

            if (!lastName.matches("[A-Za-z]+") || lastName.length() > 20) {
                invalidFields.add("Last name");
            }

            if (!gender.equals("Male") && !gender.equals("Female") && !gender.equals("Unknown")) {
                invalidFields.add("Gender");
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
                sdf.setLenient(false);
                Date dob = sdf.parse(dateOfBirth);
                if (dob.after(new Date())) {
                    invalidFields.add("Date of birth");
                }
            } catch (Exception e) {
                invalidFields.add("Date of birth");
            }

            if (!nationality.matches("[A-Za-z]{3}")) {
                invalidFields.add("Nationality");
            }

            
            if (!invalidFields.isEmpty()) {
                errors.add("Row " + rowNum + " Invalid " + String.join(", ", invalidFields));
            }

            passengers.add(new DataList(firstName, lastName, gender, dateOfBirth, nationality));
        }

        workbook.close();

        // If any errors found, throw them all at once
        if (!errors.isEmpty()) {
            throw new Exception(String.join(" | ", errors));
        }

        return passengers;
    }

    // Read cell value as String
    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    DataFormatter formatter = new DataFormatter();
                    return formatter.formatCellValue(cell);
                }
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
