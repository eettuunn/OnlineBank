package ru.hits.coreservice.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.coreservice.service.BankAccountService;
import ru.hits.coreservice.service.IntegrationBankAccountService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/integration/bank-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Интеграционные эндпоинты, связанные с банковскими счетами.")
public class IntegrationBankAccountController {

    private final IntegrationBankAccountService integrationBankAccountService;

    @GetMapping("/{id}/check-existence")
    public ResponseEntity<Boolean> checkBankAccountExistence(@PathVariable("id") UUID bankAccountId) {
        Boolean isExists = integrationBankAccountService.checkBankAccountExistenceById(bankAccountId);
        return new ResponseEntity<>(isExists, HttpStatus.OK);
    }

}
