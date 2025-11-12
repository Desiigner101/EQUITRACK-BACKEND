package io.equitrack.controller;

// DTO imports
import io.equitrack.dto.ExpenseDTO; // Expense data transfer object
import io.equitrack.dto.FilterDTO; // Filter criteria container
import io.equitrack.dto.IncomeDTO; // Income data transfer object

// Service imports
import io.equitrack.service.ExpenseService; // Business logic for expenses
import io.equitrack.service.IncomeService; // Business logic for incomes

// Spring imports
import lombok.RequiredArgsConstructor; // Auto constructor injection
import org.springframework.data.domain.Sort; // Sorting functionality
import org.springframework.http.ResponseEntity; // HTTP response wrapper
import org.springframework.web.bind.annotation.*; // Web mapping annotations

// Java time imports
import java.time.LocalDate; // Date handling
import java.util.List; // List collections

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter") // Base path for filtering operations
public class FilterController {

    private final IncomeService incomeService; // Service for income operations
    private final ExpenseService expenseService; // Service for expense operations

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter){
        // Set default values if filter fields are null
        // Default start date: earliest possible, end date: today
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();

        // Default keyword: empty string (no filtering by keyword)
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";

        // Default sort field: date, direction: ASC (ascending)
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        // Route to appropriate service based on transaction type
        if("income".equalsIgnoreCase(filter.getType())){
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        }else if("expense".equalsIgnoreCase(filter.getType())){
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        }else{
            // Error response if type is invalid
            return ResponseEntity.badRequest().body("Invalid type, Must be 'income' or 'expense' only");
        }
    }
}