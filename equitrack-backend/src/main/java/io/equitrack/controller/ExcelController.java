package io.equitrack.controller;

import io.equitrack.service.ExcelService;
import io.equitrack.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;
    private final ProfileService profileService;  // ADD THIS

    /**
     * Download income as Excel file
     */
    @GetMapping("/excel/download/income")
    public ResponseEntity<ByteArrayResource> downloadIncomeExcel() {
        try {
            Long profileId = profileService.getCurrentProfile().getId();

            byte[] excelBytes = excelService.generateIncomeExcel(profileId);
            ByteArrayResource resource = new ByteArrayResource(excelBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=income_details.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelBytes.length)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Email income Excel to user
     */
    @GetMapping("/email/income-excel")
    public ResponseEntity<Map<String, Object>> emailIncomeExcel() {
        try {
            Long profileId = profileService.getCurrentProfile().getId();
            String userEmail = profileService.getCurrentProfile().getEmail();

            excelService.sendIncomeEmail(userEmail, profileId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Income details sent to " + userEmail + " successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to email income details");

            return ResponseEntity.status(500).body(response);
        }
    }
}