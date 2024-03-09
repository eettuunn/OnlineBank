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
public class CloseBankAccountDto {

    @NotNull(message = "Идентификатор пользователя не может быть пустым")
    private UUID userId;

}
