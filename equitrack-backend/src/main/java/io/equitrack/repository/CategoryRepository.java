package io.equitrack.repository;

import io.equitrack.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    //query ani kay = select * from tbl_categories where profile_id= ?12
    List<CategoryEntity> findByProfileId(Long profileId);

    //select * from tbl_categories where id = ?1 and profile_id= ?12
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    //select * from tbl_categories where type = ?1 and profile_id= ?12
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long profileId);

}