package ru.hits.coreservice.dto;

import lombok.*;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDto {
    private UUID id;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private String additionalInformation;
    private TransactionType transactionType;
    private UUID bankAccountId;

    public TransactionDto(TransactionEntity transactionEntity) {
        this.id = transactionEntity.getId();
        this.transactionDate = transactionEntity.getTransactionDate();
        this.amount = transactionEntity.getAmount();
        this.additionalInformation = transactionEntity.getAdditionalInformation();
        this.transactionType = transactionEntity.getTransactionType();
        if (transactionEntity.getBankAccount() != null) {
            this.bankAccountId = transactionEntity.getBankAccount().getId();
        }
    }
}
