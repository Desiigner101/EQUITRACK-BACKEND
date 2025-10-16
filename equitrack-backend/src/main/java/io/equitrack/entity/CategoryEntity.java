package io.equitrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "tbl_categories")
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity {


}
