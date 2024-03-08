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
public class WithdrawMoneyDto {

    @Positive(message = "Сумма для снятия должна быть положительным числом")
    private BigDecimal amount;

    @NotNull(message = "Идентификатор пользователя не может быть пустым")
    private UUID userId;

}
