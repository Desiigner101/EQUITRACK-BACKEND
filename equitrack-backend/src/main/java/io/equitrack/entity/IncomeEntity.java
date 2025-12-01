package io.equitrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tbl_incomes")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class IncomeEntity extends TransactionEntity {

    public IncomeEntity() {
        super();
    }
}