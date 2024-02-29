package ru.hits.coreservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.coreservice.dto.BankAccountDto;
import ru.hits.coreservice.dto.CreateBankAccountDto;
import ru.hits.coreservice.service.BankAccountService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Банковские счёты.")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Operation(
            summary = "Открыть счёт."
//            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<BankAccountDto> createBankAccount(@RequestBody @Valid CreateBankAccountDto createBankAccountDto) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDto), HttpStatus.OK);
    }

}
