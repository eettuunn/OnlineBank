package ru.hits.coreservice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateBankAccountNameDto {

    @NotBlank(message = "Название счёта является обязательным к заполнению.")
    private String name;

}
