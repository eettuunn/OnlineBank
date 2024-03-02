package ru.hits.coreservice.dto;

import lombok.*;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WithdrawMoneyDto {

    @Positive(message = "Сумма для снятия должна быть положительным числом")
    private BigDecimal amount;

}
