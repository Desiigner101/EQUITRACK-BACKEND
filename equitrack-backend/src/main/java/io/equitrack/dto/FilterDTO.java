package io.equitrack.dto;

import lombok.Data;        // Lombok: Auto-generates getters, setters, toString, equals, hashCode
import java.time.LocalDate; // Date without time (for date range filtering)

/**
 * Data Transfer Object for transaction filtering criteria
 * Used to pass search and filter parameters for income/expense queries
 */
@Data
public class FilterDTO {

    // Transaction type to filter: "income", "expense", or leave empty for both
    private String type;

    // Start date for filtering range (inclusive) - null means no start limit
    private LocalDate startDate;

    // End date for filtering range (inclusive) - null means no end limit
    private LocalDate endDate;

    // Search keyword for description/name matching - null means no keyword filter
    private String keyword;

    // Field to sort results by: "date", "amount", "name" etc.
    private String sortField;

    // Sort direction: "asc" for ascending, "desc" for descending
    private String sortOrder; //<- "asc" || "desc" ->
}