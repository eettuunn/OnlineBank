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

    @NotBlank(message = "Название счёта является обязательным к заполнению.")
    private String name;

    @NotNull(message = "ID пользователя является обязательным для заполнения.")
    private UUID userId;

}
