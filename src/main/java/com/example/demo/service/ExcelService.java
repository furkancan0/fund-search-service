package com.example.demo.service;

import com.example.demo.entity.Fund;
import com.example.demo.exception.ExcelProcessingException;
import com.example.demo.exception.InvalidFileFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    public List<Fund> parseExcelFile(MultipartFile file) {
        validateFile(file);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            return parseSheet(sheet);
        } catch (IOException e) {
            throw new ExcelProcessingException("Failed to read Excel file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExcelProcessingException("Error parsing Excel file: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileFormatException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new InvalidFileFormatException("Only .xlsx files are supported");
        }
    }

    private List<Fund> parseSheet(Sheet sheet) {
        List<Fund> funds = new ArrayList<>();

        if (sheet.getLastRowNum() < 1) {
            throw new ExcelProcessingException("Excel file must contain at least one data row");
        }

        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) continue;

            try {
                Fund fund = parseFundFromRow(row);
                funds.add(fund);
            } catch (Exception e) {
                throw new ExcelProcessingException(
                        "Error parsing row " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        if (funds.isEmpty()) {
            throw new ExcelProcessingException("No valid fund data found in Excel file");
        }
        return funds;
    }

    private Fund parseFundFromRow(Row row) {
        String fundCode = getCellValueAsString(row.getCell(0));
        String fundName = getCellValueAsString(row.getCell(1));
        String fundType = getCellValueAsString(row.getCell(2));

        if (fundCode.isEmpty() || fundName.isEmpty() || fundType.isEmpty()) {
            throw new ExcelProcessingException(
                    "Fund Code, Fund Name, and Fund Type are required fields");
        }

        return Fund.builder()
                .fundCode(fundCode)
                .fundName(fundName)
                .fundType(fundType)
                .oneMonth(getCellValueAsBigDecimal(row.getCell(3)))
                .threeMonths(getCellValueAsBigDecimal(row.getCell(4)))
                .sixMonths(getCellValueAsBigDecimal(row.getCell(5)))
                .newYear(getCellValueAsBigDecimal(row.getCell(6)))
                .oneYear(getCellValueAsBigDecimal(row.getCell(7)))
                .threeYears(getCellValueAsBigDecimal(row.getCell(8)))
                .fiveYears(getCellValueAsBigDecimal(row.getCell(9)))
                .build();
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 3; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;

        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                try {
                    String value = cell.getStringCellValue().trim();
                    yield value.isEmpty() ? null : new BigDecimal(value);
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
}