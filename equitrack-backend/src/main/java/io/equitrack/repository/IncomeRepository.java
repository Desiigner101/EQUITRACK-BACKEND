package io.equitrack.repository;

import io.equitrack.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository para sa IncomeEntity database operations
 * Nag-provide og specialized queries para sa income tracking, reporting, ug analytics
 */
public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {
    // Na-inherit gikan sa JpaRepository: save(), findById(), findAll(), delete(), count(), etc.

    /**
     * GET ALL INCOMES FOR USER, NEWEST FIRST / Kuhaa tanang kita sa user, pinakabag-o una
     * SQL: SELECT * FROM tbl_incomes WHERE profile_id = ? ORDER BY date DESC
     * Used in: Income history page, transaction lists, LINE CHARTS
     */
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    /**
     * GET RECENT 5 INCOMES FOR USER, NEWEST FIRST / Kuhaa 5 ka pinakabag-o nga kita
     * SQL: SELECT * FROM tbl_incomes WHERE profile_id = ? ORDER BY date DESC LIMIT 5
     * Used in: Dashboard recent activity, quick overview
     */
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    /**
     * CUSTOM QUERY: GET TOTAL INCOME AMOUNT FOR USER / Total nga kita sa user
     * NOTE: Ang method name sayop - dapat findTotalIncomeByProfileId pero gi-maintain nato ang existing code
     * SQL: SELECT SUM(amount) FROM tbl_incomes WHERE profile_id = ?
     * Used in: Dashboard totals, financial summaries
     */
    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    /**
     * ADVANCED FILTERING WITH SEARCH AND SORTING / Advanced nga pag-filter ug pag-search
     * Combines date range, keyword search, and dynamic sorting
     * Used in: Income reports, search functionality, filtering
     */
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    /**
     * GET INCOMES BY DATE RANGE FOR USER / Kuhaa kita base sa date range
     * SQL: SELECT * FROM tbl_incomes WHERE profile_id = ? AND date BETWEEN ? AND ?
     * Used in: Monthly reports, period analysis
     */
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}