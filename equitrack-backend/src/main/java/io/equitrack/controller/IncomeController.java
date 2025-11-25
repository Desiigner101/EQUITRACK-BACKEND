package io.equitrack.controller;

import io.equitrack.dto.ExpenseDTO;
import io.equitrack.dto.IncomeDTO;
import io.equitrack.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Makes this class handle web requests and return JSON responses automatically
@RequestMapping("/incomes")  // All URLs start with /incomes - this is the API endpoint
@RequiredArgsConstructor  // Automatically creates constructor to inject IncomeService dependency
public class IncomeController {

    private final IncomeService incomeService;  // Connects to business logic layer - this is where actual work happens

    // When client POSTs new income data to /incomes
    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto){
        // incomeService.addIncome() will validate data, check user permissions, save to database
        IncomeDTO saved = incomeService.addIncome(dto);
        // Returns HTTP 201 (Created) with the saved income including generated ID and timestamps
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<IncomeDTO>> getAllIncomes(){
        List<IncomeDTO> allIncomes = incomeService.getAllIncomesForCurrentUser();
        return ResponseEntity.ok(allIncomes);
    }

    // When client GETs /incomes - returns list of incomes for current user this month
    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getIncomes(){
        // incomeService automatically filters by current logged-in user and current month
        List<IncomeDTO> expenses = incomeService.getCurrentMonthIncomesForCurrentUser();
        // Returns HTTP 200 (OK) with JSON array of income objects
        return ResponseEntity.ok(expenses);
    }

    // When client DELETEs /incomes/{id} - removes specific income record
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id){
        // incomeService checks if user owns this income, then deletes from database
        incomeService.deleteIncome(id);
        // Returns HTTP 204 (No Content) - successful deletion with empty response body
        return ResponseEntity.noContent().build();
    }

}