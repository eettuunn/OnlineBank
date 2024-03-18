package ru.hits.coreservice.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.coreservice.enumeration.TransactionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(precision = 100, scale = 50)
    private BigDecimal amount;

    private String additionalInformation;

    @Column(name = "operation_type")
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccountEntity bankAccount;

}
