package ru.hits.coreservice.dto;

import lombok.*;

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

    @NotNull(message = "ID пользователя является обязательным для заполнения.")
    private UUID userId;

}
