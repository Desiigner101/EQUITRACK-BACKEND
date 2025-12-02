package io.equitrack.repository;

import io.equitrack.entity.BudgetEntity;
import io.equitrack.entity.CategoryEntity;
import io.equitrack.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {
    List<BudgetEntity> findByProfile(ProfileEntity profile);
    List<BudgetEntity> findByProfileAndPeriod(ProfileEntity profile, String period);
    Optional<BudgetEntity> findByProfileAndCategory(ProfileEntity profile, CategoryEntity category);
}