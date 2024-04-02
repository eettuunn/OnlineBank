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
public class DepositMoneyDto {

    @Positive(message = "Сумма для пополнения должна быть положительным числом")
    private BigDecimal amount;

    @NotNull(message = "Тип транзакции не может быть пустым")
    private DepositTransactionType transactionType;

    @NotBlank(message = "Валюта является обязательной к заполнению")
    private String currencyCode;

}
