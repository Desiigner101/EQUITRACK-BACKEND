package io.equitrack.repository;

import io.equitrack.entity.ExpenseEntity;
import io.equitrack.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    //query ani kay: select * from tbl_incomes where profile id = ?1 order date descending order
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_incomes where profile_id ?!@ order by date descending nya limit by 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);


    List<IncomeEntity>findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
