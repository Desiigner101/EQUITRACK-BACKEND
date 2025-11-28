package io.equitrack.controller;

import io.equitrack.dto.ExpenseDTO;
import io.equitrack.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Makes this class handle web requests and return JSON responses automatically
@RequestMapping("/expenses")  // All URLs start with /expenses - this is the API endpoint
@RequiredArgsConstructor  // Automatically creates constructor to inject ExpenseService dependency
public class ExpenseController {

    private final ExpenseService expenseService;  // Connects to business logic layer - this is where actual work happens

    // When client POSTs new expense data to /expenses/all
    @PostMapping("/all")
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto){
        // expenseService.addExpense() will validate data, check user permissions, save to database
        ExpenseDTO saved = expenseService.addExpense(dto);
        // Returns HTTP 201 (Created) with the saved expense including generated ID and timestamps
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(){
        List<ExpenseDTO> allExpenses = expenseService.getAllExpensesForCurrentUser();
        return ResponseEntity.ok(allExpenses);
    }

    // When client GETs /expenses - returns list of expenses for current user this month
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses(){
        // expenseService automatically filters by current logged-in user and current month
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        // Returns HTTP 200 (OK) with JSON array of expense objects
        return ResponseEntity.ok(expenses);
    }

    // When client DELETEs /expenses/{id} - removes specific expense record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        // expenseService checks if user owns this expense, then deletes from database
        expenseService.deleteExpense(id);
        // Returns HTTP 204 (No Content) - successful deletion with empty response body
        return ResponseEntity.noContent().build();
    }

}