package io.equitrack.service;

import io.equitrack.entity.IncomeEntity;
import io.equitrack.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelService {

    private final IncomeRepository incomeRepository;
    private final EmailService emailService;

    /**
     * Generate Excel file from income data
     */
    public byte[] generateIncomeExcel(Long profileId) {
        try {
            List<IncomeEntity> incomes = incomeRepository.findByProfileIdOrderByDateDesc(profileId);

            // Use try-with-resources to ensure stream & workbook close properly
            try (Workbook workbook = new XSSFWorkbook();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                Sheet sheet = workbook.createSheet("Income Details");

                // Header style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Name", "Amount", "Date", "Category"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, 25 * 256);
                }

                // Data rows
                int rowNum = 1;
                double total = 0.0;
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");

                for (IncomeEntity income : incomes) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(income.getName());
                    row.createCell(1).setCellValue(income.getAmount().doubleValue());
                    row.createCell(2).setCellValue(income.getDate().format(dateFormat));
                    row.createCell(3).setCellValue(
                            income.getCategory() != null ? income.getCategory().getName() : "Uncategorized"
                    );
                    total += income.getAmount().doubleValue();
                }

                // Total row
                Row totalRow = sheet.createRow(rowNum + 1);
                CellStyle totalStyle = workbook.createCellStyle();
                Font totalFont = workbook.createFont();
                totalFont.setBold(true);
                totalStyle.setFont(totalFont);

                totalRow.createCell(0).setCellValue("TOTAL");
                Cell totalAmountCell = totalRow.createCell(1);
                totalAmountCell.setCellValue(total);
                totalRow.getCell(0).setCellStyle(totalStyle);
                totalAmountCell.setCellStyle(totalStyle);

                // Write and return bytes
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }

        } catch (Exception e) {
            log.error("❌ Error generating Excel for profile {}: {}", profileId, e.getMessage());
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }

    /**
     * Send income Excel via email
     */
    public void sendIncomeEmail(String userEmail, Long profileId) {
        // Generate Excel
        byte[] excelBytes = generateIncomeExcel(profileId);

        // Create email body
        String emailBody = """
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #4CAF50;">Your Income Details</h2>
                <p>Hello,</p>
                <p>Please find your income details attached to this email.</p>
                <p>Best regards,<br><strong>EquiTrack Team</strong></p>
            </body>
            </html>
            """;

        // Send email with attachment
        emailService.sendEmailWithExcel(
                userEmail,
                "Your Income Details Report",
                emailBody,
                excelBytes,
                "income_details.xlsx"
        );

        log.info("✅ Income email sent to: {}", userEmail);
    }
}