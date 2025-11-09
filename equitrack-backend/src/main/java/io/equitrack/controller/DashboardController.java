package io.equitrack.controller;

// Service imports
import io.equitrack.service.DashboardService; // Business logic for dashboard operations

// Spring imports
import lombok.RequiredArgsConstructor; // Auto-generates constructor for dependency injection
import org.springframework.http.ResponseEntity; // Wrapper for HTTP response with status code
import org.springframework.web.bind.annotation.*; // All Spring web annotations (@RestController, @GetMapping, etc.)

// Java util imports
import java.util.Map; // Key-value pair collection for flexible JSON response

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService; // Service nga mo-handle sa tanan dashboard logic

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData(){
        // Here makuha ang complete dashboard data for the current user
        Map<String, Object> dashboardData = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData); // Balik ang data with OK status
    }
}