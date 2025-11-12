package io.equitrack.controller;

// DTO and Service imports
import io.equitrack.dto.ExpenseDTO; // Data transfer object for expense information
import io.equitrack.service.ExpenseService; // Business logic for expense operations

// Spring and Lombok imports
import lombok.RequiredArgsConstructor; // Auto-generates constructor
import org.springframework.http.HttpStatus; // HTTP status codes
import org.springframework.http.ResponseEntity; // HTTP response wrapper
import org.springframework.web.bind.annotation.*; // Web mapping annotations

// Java util imports
import java.util.List; // Collection for expense lists

@RestController // Handles REST API requests
@RequiredArgsConstructor // Auto-injects dependencies
@RequestMapping("/expenses") // Base path for all expense endpoints
public class ExpenseController {

    private final ExpenseService expenseService; // Service nga mo-manage sa expense operations

    // CREATE - Add new expense record
    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto){
        ExpenseDTO saved = expenseService.addExpense(dto); // Save the new expense
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); // Return 201 status
    }

    // READ - Get current month's expenses
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses(){
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser(); // Kuhaa current month expenses
        return ResponseEntity.ok(expenses); // Return 200 OK with expense list
    }

    // DELETE - Remove expense by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpense(id); // Delete the expense record
        return ResponseEntity.noContent().build(); // Return 204 No Content (successful delete)
    }
}