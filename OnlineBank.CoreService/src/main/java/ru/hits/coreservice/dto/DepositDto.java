package ru.hits.coreservice.dto;

import lombok.*;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepositDto {
    @Positive(message = "Сумма для пополнения должна быть положительным числом")
    private BigDecimal amount;
}
