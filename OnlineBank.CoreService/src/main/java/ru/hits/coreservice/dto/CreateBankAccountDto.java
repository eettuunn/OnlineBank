package ru.hits.coreservice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateBankAccountDto {

    @NotBlank(message = "Название счёта является обязательным к заполнению")
    private String name;

    @NotBlank(message = "Валюта счёта является обязательной к заполнению")
    private String currencyCode;

}
