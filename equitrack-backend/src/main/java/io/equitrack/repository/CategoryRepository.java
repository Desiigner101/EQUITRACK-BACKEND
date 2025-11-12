package io.equitrack.repository;

import io.equitrack.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPA base interface
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CategoryEntity database operations
 * Extends JpaRepository to inherit standard CRUD operations
 * Spring Data JPA automatically implements these methods at runtime
 */
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    // JpaRepository provides: save(), findById(), findAll(), deleteById(), count(), etc.

    /**
     * FIND ALL CATEGORIES FOR A SPECIFIC USER
     * SQL: SELECT * FROM tbl_categories WHERE profile_id = ?
     * Used in: Dashboard, category management page
     */
    List<CategoryEntity> findByProfileId(Long profileId);

    /**
     * FIND SPECIFIC CATEGORY THAT BELONGS TO A USER (Security check)
     * SQL: SELECT * FROM tbl_categories WHERE id = ? AND profile_id = ?
     * Used in: Update/Delete operations to verify ownership
     */
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    /**
     * FIND CATEGORIES BY TYPE FOR A SPECIFIC USER
     * SQL: SELECT * FROM tbl_categories WHERE type = ? AND profile_id = ?
     * Used in: Filtering categories by "income" or "expense" type
     */
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    /**
     * CHECK IF CATEGORY NAME ALREADY EXISTS FOR A USER
     * SQL: SELECT COUNT(*) > 0 FROM tbl_categories WHERE name = ? AND profile_id = ?
     * Used in: Category creation/update to prevent duplicates
     */
    Boolean existsByNameAndProfileId(String name, Long profileId);
}