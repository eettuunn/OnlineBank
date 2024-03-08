package ru.hits.coreservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.coreservice.dto.CreateTransactionDto;
import ru.hits.coreservice.dto.TransactionDto;
import ru.hits.coreservice.service.IntegrationTransactionService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/integration/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Интеграционные эндпоинты, связанные с банковскими операциями по счетам.")
public class IntegrationTransactionController {

    private final IntegrationTransactionService integrationTransactionService;

    @Operation(
            summary = "Создать банковскую операцию."
//            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody @Valid CreateTransactionDto createTransactionDto) {
        return new ResponseEntity<>(integrationTransactionService.createTransaction(createTransactionDto), HttpStatus.OK);
    }

}
