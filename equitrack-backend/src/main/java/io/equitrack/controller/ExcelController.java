package io.equitrack.controller;

import io.equitrack.service.ExcelService;
import io.equitrack.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class ExcelController {

    private final ExcelService excelService;
    private final ProfileService profileService;

    @GetMapping(value = "excel/download/income",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<ByteArrayResource> downloadIncomeExcel() {
        try {
            log.info("📥 Download income excel request received");

            Long profileId = profileService.getCurrentProfile().getId();
            log.info("👤 Profile ID: {}", profileId);

            byte[] excelBytes = excelService.generateIncomeExcel(profileId);
            log.info("✅ Excel generated: {} bytes", excelBytes.length);

            ByteArrayResource resource = new ByteArrayResource(excelBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=income_details.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelBytes.length)
                    .body(resource);

        } catch (SecurityException se) {
            log.error("❌ Unauthorized: ", se);
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("❌ Error downloading excel: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("email/income-excel")
    public ResponseEntity<Map<String, Object>> emailIncomeExcel() {
        try {
            log.info("📧 Email income excel request received");

            Long profileId = profileService.getCurrentProfile().getId();
            String userEmail = profileService.getCurrentProfile().getEmail();
            log.info("👤 Sending to: {} (Profile ID: {})", userEmail, profileId);

            excelService.sendIncomeEmail(userEmail, profileId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Income details sent to " + userEmail + " successfully");

            return ResponseEntity.ok(response);

        } catch (SecurityException se) {
            log.error("❌ Unauthorized: ", se);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            log.error("❌ Error emailing excel: ", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to email income details");

            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping(value = "excel/download/expense",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<ByteArrayResource> downloadExpenseExcel() {
        try {
            log.info("📥 Download expense excel request received");

            Long profileId = profileService.getCurrentProfile().getId();
            log.info("👤 Profile ID: {}", profileId);

            byte[] excelBytes = excelService.generateExpenseExcel(profileId);
            log.info("✅ Excel generated: {} bytes", excelBytes.length);

            ByteArrayResource resource = new ByteArrayResource(excelBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expense_details.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelBytes.length)
                    .body(resource);

        } catch (SecurityException se) {
            log.error("❌ Unauthorized: ", se);
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("❌ Error downloading excel: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("email/expense-excel")
    public ResponseEntity<Map<String, Object>> emailExpenseExcel() {
        try {
            log.info("📧 Email expense excel request received");

            Long profileId = profileService.getCurrentProfile().getId();
            String userEmail = profileService.getCurrentProfile().getEmail();
            log.info("👤 Sending to: {} (Profile ID: {})", userEmail, profileId);

            excelService.sendExpenseEmail(userEmail, profileId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Expense details sent to " + userEmail + " successfully");

            return ResponseEntity.ok(response);

        } catch (SecurityException se) {
            log.error("❌ Unauthorized: ", se);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Unauthorized");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            log.error("❌ Error emailing excel: ", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to email expense details");

            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("excel/test")
    public ResponseEntity<String> test() {
        log.info("🧪 Test endpoint hit!");
        return ResponseEntity.ok("Excel controller is working!");
    }
}