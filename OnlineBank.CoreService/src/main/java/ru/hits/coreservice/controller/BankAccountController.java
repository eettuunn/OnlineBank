package ru.hits.coreservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.coreservice.dto.BankAccountDto;
import ru.hits.coreservice.dto.CreateBankAccountDto;
import ru.hits.coreservice.dto.DepositDto;
import ru.hits.coreservice.security.JWTUtil;
import ru.hits.coreservice.service.BankAccountService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Банковские счёты.")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    private final JWTUtil jwtUtil;

    @Operation(
            summary = "Открыть банковский счёт.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/open")
    public ResponseEntity<BankAccountDto> createBankAccount(@RequestBody @Valid CreateBankAccountDto createBankAccountDto) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Закрыть банковский счёт.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/close")
    public ResponseEntity<BankAccountDto> closeBankAccount(@PathVariable("id") UUID bankAccountId) {
        return new ResponseEntity<>(bankAccountService.closeBankAccount(bankAccountId), HttpStatus.OK);
    }

    @Operation(
            summary = "Пополнить банковский счёт.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/deposit")
    public ResponseEntity<BankAccountDto> depositMoney(@PathVariable("id") UUID bankAccountId,
                                                       @RequestBody @Valid DepositDto depositDto) {
        return new ResponseEntity<>(bankAccountService.depositMoney(bankAccountId, depositDto.getAmount()), HttpStatus.OK);
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        return new ResponseEntity<>(jwtUtil.generateToken(UUID.fromString("77141e72-da79-44c8-b057-ea1ea39bac2a")), HttpStatus.OK);
    }

}
