package ru.hits.coreservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.LoanTransactionType;
import ru.hits.coreservice.enumeration.TransactionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateTransactionDto {

    @NotNull(message = "Сумма транзакции не может быть пустой")
    private BigDecimal amount;

    private String additionalInformation;

    @NotNull(message = "Тип транзакции не может быть пустым")
    private LoanTransactionType transactionType;

    @NotNull(message = "Идентификатор банковского счета не может быть пустым")
    private UUID bankAccountId;

    @NotNull(message = "Идентификатор пользователя не может быть пустым")
    private UUID userId;

}
