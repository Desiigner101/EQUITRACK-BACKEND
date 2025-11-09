package io.equitrack.repository;

import io.equitrack.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;          // Sorting and pagination
import org.springframework.data.jpa.repository.JpaRepository; // Base CRUD operations
import org.springframework.data.jpa.repository.Query; // Custom SQL queries
import org.springframework.data.repository.query.Param; // Named parameter binding
import java.math.BigDecimal;                     // Precise monetary calculations
import java.time.LocalDate;                      // Date without time
import java.util.List;

/**
 * Repository interface for ExpenseEntity database operations
 * Provides specialized queries for expense tracking, reporting, and analytics
 */
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    // Inherits: save(), findById(), findAll(), delete(), count(), etc.

    /**
     * GET ALL EXPENSES FOR USER, NEWEST FIRST
     * SQL: SELECT * FROM tbl_expenses WHERE profile_id = ? ORDER BY date DESC
     * Used in: Expense history page, transaction lists
     */
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    /**
     * GET RECENT 5 EXPENSES FOR USER, NEWEST FIRST
     * SQL: SELECT * FROM tbl_expenses WHERE profile_id = ? ORDER BY date DESC LIMIT 5
     * Used in: Dashboard recent activity, quick overview
     */
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    /**
     * CUSTOM QUERY: GET TOTAL EXPENSE AMOUNT FOR USER
     * SQL: SELECT SUM(amount) FROM tbl_expenses WHERE profile_id = ?
     * Used in: Dashboard totals, financial summaries
     */
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    /**
     * ADVANCED FILTERING WITH SEARCH AND SORTING
     * Combines date range, keyword search, and dynamic sorting
     * Used in: Expense reports, search functionality, filtering
     */
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    /**
     * GET EXPENSES BY DATE RANGE FOR USER
     * SQL: SELECT * FROM tbl_expenses WHERE profile_id = ? AND date BETWEEN ? AND ?
     * Used in: Monthly reports, period analysis
     */
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    /**
     * GET EXPENSES FOR SPECIFIC DATE
     * SQL: SELECT * FROM tbl_expenses WHERE profile_id = ? AND date = ?
     * Used in: Daily expense tracking, calendar views
     */
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);
}