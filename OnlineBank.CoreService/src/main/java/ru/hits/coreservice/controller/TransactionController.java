package ru.hits.coreservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.coreservice.dto.TransactionDto;
import ru.hits.coreservice.service.TransactionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Операции.")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Посмотреть историю операций по банковскому счёту.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/bank-account/{id}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByBankAccountId(@PathVariable("id") UUID bankAccountId) {
        List<TransactionDto> transactionsDtos = transactionService.getTransactionsByBankAccountId(bankAccountId);
        return new ResponseEntity<>(transactionsDtos, HttpStatus.OK);
    }

}
