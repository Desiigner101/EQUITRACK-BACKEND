package io.equitrack.service;

import io.equitrack.dto.ExpenseDTO;
import io.equitrack.dto.IncomeDTO;
import io.equitrack.dto.RecentTransactionDTO;
import io.equitrack.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    // Dependencies for accessing income, expense, and user data
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;
    private final WalletService walletService;

    /**
     * AGGREGATES ALL DASHBOARD DATA INTO SINGLE RESPONSE
     *
     * This is the MAIN DASHBOARD method that combines data from multiple services
     * to create a complete financial overview for the user's dashboard
     */
    public Map<String, Object> getDashboardData(){
        // Get current user for data isolation
        ProfileEntity profile = profileService.getCurrentProfile();

        // Extract profile ID once to avoid repeated method calls
        Long profileId = profile.getId();

        // Use LinkedHashMap to maintain response order
        Map<String, Object> returnValue = new LinkedHashMap<>();

        // Get recent transactions from both services
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomeForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        /**
         * CREATE UNIFIED RECENT TRANSACTIONS LIST
         *
         * This combines incomes and expenses into a single timeline,
         * sorted by date (newest first) with creation time as tiebreaker
         */
        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                        // Convert incomes to RecentTransactionDTO
                        latestIncomes.stream().map(income ->
                                RecentTransactionDTO.builder()
                                        .id(income.getId())
                                        .profileId(profileId)  // Use the extracted profileId
                                        .icon(income.getIcon())
                                        .name(income.getName())
                                        .amount(income.getAmount())
                                        .date(income.getDate())
                                        .createdAt(income.getCreatedAt())
                                        .updatedAt(income.getUpdatedAt())
                                        .type("income")  // Mark as income type
                                        .build()),

                        // Convert expenses to RecentTransactionDTO
                        latestExpenses.stream().map(expense ->
                                RecentTransactionDTO.builder()
                                        .id(expense.getId())
                                        .profileId(profileId)  // Use the extracted profileId
                                        .icon(expense.getIcon())
                                        .name(expense.getName())
                                        .amount(expense.getAmount())
                                        .date(expense.getDate())
                                        .createdAt(expense.getCreatedAt())
                                        .updatedAt(expense.getUpdatedAt())
                                        .type("expense")  // Mark as expense type
                                        .build()))

                // Sort by date (newest first), then by creation time if dates are equal
                .sorted((a, b) -> {
                    int comp = b.getDate().compareTo(a.getDate());  // Compare dates (newest first)

                    // If same date, use creation time as tiebreaker
                    if(comp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());  // Newer creations first
                    }
                    return comp;
                })
                .collect(Collectors.toList());

        /**
         * CALCULATE FINANCIAL TOTALS
         */

        // Total Balance = Total Income - Total Expenses
        returnValue.put("totalBalance",
                incomeService.getTotalIncomeForCurrentUser()
                        .subtract(expenseService.getTotalExpensesForCurrentUser()));

        // Individual totals for detailed breakdown
        returnValue.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpense", expenseService.getTotalExpensesForCurrentUser());

        /**
         * WALLET DATA - NEW FEATURE
         */
        returnValue.put("wallets", walletService.getActiveWallets(profileId));
        returnValue.put("totalWalletBalance", walletService.getTotalBalance(profileId));

        /**
         * RECENT ACTIVITY DATA
         */
        returnValue.put("recent5Expenses", latestExpenses);
        returnValue.put("recent5Incomes", latestIncomes);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;
    }
}