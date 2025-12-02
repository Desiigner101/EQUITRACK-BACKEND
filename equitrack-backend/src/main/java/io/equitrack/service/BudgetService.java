package io.equitrack.service;

import io.equitrack.entity.BudgetEntity;
import io.equitrack.entity.CategoryEntity;
import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.BudgetRepository;
import io.equitrack.repository.CategoryRepository;
import io.equitrack.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Create new budget
    @Transactional
    public BudgetEntity createBudget(Long profileId, Long categoryId, BigDecimal limitAmount, String period, String description) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Check if budget already exists for this profile and category
        Optional<BudgetEntity> existingBudget = budgetRepository.findByProfileAndCategory(profile, category);
        if (existingBudget.isPresent()) {
            throw new RuntimeException("Budget already exists for this category");
        }

        if (limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Budget limit must be greater than zero");
        }

        if (!period.equals("MONTHLY") && !period.equals("WEEKLY")) {
            throw new RuntimeException("Period must be either MONTHLY or WEEKLY");
        }

        BudgetEntity budget = BudgetEntity.builder()
                .profile(profile)
                .category(category)
                .limitAmount(limitAmount)
                .period(period)
                .description(description)
                .build();

        return budgetRepository.save(budget);
    }

    // Get all budgets for a profile
    public List<BudgetEntity> getBudgetsByProfile(Long profileId) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return budgetRepository.findByProfile(profile);
    }

    // Get budget by ID
    public Optional<BudgetEntity> getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId);
    }

    // Update budget
    @Transactional
    public BudgetEntity updateBudget(Long budgetId, BigDecimal limitAmount, String period, String description) {
        BudgetEntity budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (limitAmount != null && limitAmount.compareTo(BigDecimal.ZERO) > 0) {
            budget.setLimitAmount(limitAmount);
        }

        if (period != null) {
            if (!period.equals("MONTHLY") && !period.equals("WEEKLY")) {
                throw new RuntimeException("Period must be either MONTHLY or WEEKLY");
            }
            budget.setPeriod(period);
        }

        if (description != null) {
            budget.setDescription(description);
        }

        return budgetRepository.save(budget);
    }

    // Delete budget
    @Transactional
    public void deleteBudget(Long budgetId) {
        if (!budgetRepository.existsById(budgetId)) {
            throw new RuntimeException("Budget not found");
        }
        budgetRepository.deleteById(budgetId);
    }

    // Get budgets by period
    public List<BudgetEntity> getBudgetsByPeriod(Long profileId, String period) {
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return budgetRepository.findByProfileAndPeriod(profile, period);
    }
}