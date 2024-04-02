package ru.hits.coreservice.dto;

import lombok.*;

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
public class TransferMoneyDto {

    @NotNull(message = "Идентификатор банковского счета, с которого отправляются деньги, не может быть пустым")
    private UUID fromAccountId;

    @Positive(message = "Сумма для перевода должна быть положительным числом")
    private BigDecimal amount;

    @NotBlank(message = "Валюта является обязательной к заполнению")
    private String currencyCode;

}
