package io.equitrack.service;

import io.equitrack.dto.IncomeDTO;
import io.equitrack.entity.CategoryEntity;
import io.equitrack.entity.IncomeEntity;
import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.CategoryRepository;
import io.equitrack.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    // Database access for categories and incomes
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;

    // User context and security
    private final ProfileService profileService;

    //--- HELPER METHODS: DATA TRANSFORMATION ---

    /**
     * CONVERT DTO TO ENTITY - For database operations
     * Links income to user profile and category
     */
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
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
    private IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
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
     * CREATE NEW INCOME
     * Business rules:
     * - Must belong to current user
     * - Must link to valid category
     * - Automatic user context from security
     */
    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        IncomeEntity newIncome = toEntity(dto, profile, category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    /**
     * GET CURRENT MONTH'S INCOMES
     * Automatically calculates month boundaries
     * Only returns current user's data
     */
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);      // First day of month
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth()); // Last day of month

        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(
                profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    /**
     * DELETE INCOME WITH OWNERSHIP VERIFICATION
     * Security: User can only delete their own incomes
     */
    public void deleteIncome(Long incomeId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found!"));

        // Ownership verification
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this income!");
        }
        incomeRepository.delete(entity);
    }

    /**
     * GET 5 MOST RECENT INCOMES
     * Used for: Dashboard widgets, recent activity
     */
    public List<IncomeDTO> getLatest5IncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    /**
     * GET TOTAL INCOME AMOUNT
     * Uses database SUM operation for performance
     * Returns BigDecimal for financial precision
     */
    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total: BigDecimal.ZERO;  // Handle null results
    }

    /**
     * ADVANCED INCOME FILTERING
     * Combines date range, keyword search, and sorting
     */
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    /**
     * GET ALL INCOMES FOR CURRENT USER (ALL MONTHS)
     * Used for: Line charts, historical analysis, income trends
     * Returns all income records across all time periods
     */
    public List<IncomeDTO> getAllIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdOrderByDateDesc(profile.getId());
        return incomes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}