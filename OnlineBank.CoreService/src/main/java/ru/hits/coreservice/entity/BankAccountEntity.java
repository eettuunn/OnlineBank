package ru.hits.coreservice.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "bank_account")
public class BankAccountEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String name;

    private String number;

    private BigDecimal balance;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "is_closed")
    private Boolean isClosed;

    @OneToMany(mappedBy = "bankAccount")
    @OrderBy("transactionDate DESC")
    private List<TransactionEntity> transactions;

}
