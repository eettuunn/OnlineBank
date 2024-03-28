package ru.hits.coreservice.dto;

import lombok.*;
import ru.hits.coreservice.enumeration.DepositTransactionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateTransactionMessage {

    private BigDecimal amount;

    private String transactionType;

    private UUID bankAccountId;

    private UUID fromBankAccountId;

    private UUID toBankAccountId;

    private String currencyCode;

}
