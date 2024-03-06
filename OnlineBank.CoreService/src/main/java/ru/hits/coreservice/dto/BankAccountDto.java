package ru.hits.coreservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.hits.coreservice.entity.BankAccountEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankAccountDto {

    private UUID id;
    private String name;
    private String number;
    private BigDecimal balance;
    private UUID ownerId;
    private Boolean isClosed;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime creationDate;

    private List<TransactionDto> transactions;

    public BankAccountDto(BankAccountEntity bankAccount) {
        this.id = bankAccount.getId();
        this.name = bankAccount.getName();
        this.number = bankAccount.getNumber();
        this.balance = bankAccount.getBalance();
        this.ownerId = bankAccount.getOwnerId();
        this.isClosed = bankAccount.getIsClosed();
        this.creationDate = bankAccount.getCreationDate();
        this.transactions = bankAccount.getTransactions().stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }

}
