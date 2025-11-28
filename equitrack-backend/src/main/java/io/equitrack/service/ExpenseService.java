package io.equitrack.service;

import io.equitrack.dto.ExpenseDTO;
import io.equitrack.entity.CategoryEntity;
import io.equitrack.entity.ExpenseEntity;
import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.CategoryRepository;
import io.equitrack.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    // Database access for categories and expenses
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    // User context and security
    private final ProfileService profileService;

    //--- HELPER METHODS: DATA TRANSFORMATION ---

    /**
     * CONVERT DTO TO ENTITY - For database operations
     * Links expense to user profile and category
     */
    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)      // Set owning user
                .category(category)    // Set category relationship
                .build();
    }

    /**
     * CONVERT ENTITY TO DTO - For API responses
     * Includes category names for easy frontend display
     */
    private ExpenseDTO toDTO(ExpenseEntity entity){
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    //--- CORE BUSINESS OPERATIONS ---

    /**
     * CREATE NEW EXPENSE
     * Business rules:
     * - Must belong to current user
     * - Must link to valid category
     * - Automatic user context from security
     */
    public ExpenseDTO addExpense(ExpenseDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        ExpenseEntity newExpense = toEntity(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    /**
     * GET CURRENT MONTH'S EXPENSES
     * Automatically calculates month boundaries
     * Only returns current user's data
     */
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);      // First day of month
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth()); // Last day of month

        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(
                profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    /**
     * DELETE EXPENSE WITH OWNERSHIP VERIFICATION
     * Security: User can only delete their own expenses
     */
    public void deleteExpense(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found!"));

        // Ownership verification
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense!");
        }
        expenseRepository.delete(entity);
    }

    /**
     * GET 5 MOST RECENT EXPENSES
     * Used for: Dashboard widgets, recent activity
     */
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    /**
     * GET TOTAL EXPENSES AMOUNT
     * Uses database SUM operation for performance
     * Returns BigDecimal for financial precision
     */
    public BigDecimal getTotalExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total: BigDecimal.ZERO;  // Handle null results
    }

    /**
     * ADVANCED EXPENSE FILTERING
     * Combines date range, keyword search, and sorting
     */
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    /**
     * GET EXPENSES FOR SPECIFIC DATE
     * Used for: Daily reports, notifications, calendar views
     */
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date){
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDTO).toList();
    }

    /**
     * GET ALL EXPENSES FOR CURRENT USER
     * Returns complete expense history (no date filtering)
     * Sorted by date in descending order (newest first)
     */
    public List<ExpenseDTO> getAllExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }
}