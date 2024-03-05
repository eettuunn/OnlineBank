package ru.hits.coreservice.dto;

import lombok.*;
import ru.hits.coreservice.entity.BankAccountEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankAccountWithoutTransactionsDto {

    private UUID id;
    private String name;
    private String number;
    private BigDecimal balance;
    private UUID ownerId;
    private Boolean isClosed;

    public BankAccountWithoutTransactionsDto(BankAccountEntity bankAccount) {
        this.id = bankAccount.getId();
        this.name = bankAccount.getName();
        this.number = bankAccount.getNumber();
        this.balance = bankAccount.getBalance();
        this.ownerId = bankAccount.getOwnerId();
        this.isClosed = bankAccount.getIsClosed();
    }

}
