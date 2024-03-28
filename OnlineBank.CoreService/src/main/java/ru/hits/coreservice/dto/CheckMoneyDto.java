package ru.hits.coreservice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckMoneyDto {

    @Positive(message = "Сумма должна быть положительным числом")
    private BigDecimal amount;

    @NotBlank(message = "Валюта является обязательной к заполнению")
    private String currencyCode;

}
