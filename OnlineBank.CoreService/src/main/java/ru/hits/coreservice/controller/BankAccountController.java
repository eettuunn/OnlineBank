package ru.hits.coreservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.coreservice.dto.*;
import ru.hits.coreservice.enumeration.SortDirection;
import ru.hits.coreservice.security.JWTUtil;
import ru.hits.coreservice.service.BankAccountService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Банковские счета.")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    private final JWTUtil jwtUtil;

    @Operation(
            summary = "Посмотреть банковские счета всех клиентов.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<BankAccountWithoutTransactionsDto>> getAllBankAccounts(@RequestParam(defaultValue = "ASC") SortDirection creationDateSortDirection) {
        return new ResponseEntity<>(bankAccountService.getAllBankAccounts(creationDateSortDirection.toSortDirection()), HttpStatus.OK);
    }

    @Operation(
            summary = "Посмотреть банковские счета клиента.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/owner/{id}")
    public ResponseEntity<List<BankAccountWithoutTransactionsDto>> getBankAccountsByOwnerId(@RequestParam(defaultValue = "ASC") SortDirection creationDateSortDirection,
                                                                         @PathVariable("id") UUID ownerId) {
        return new ResponseEntity<>(bankAccountService.getBankAccountsByOwnerId(ownerId, creationDateSortDirection.toSortDirection()), HttpStatus.OK);
    }

    @Operation(
            summary = "Посмотреть информацию о банковском счёте.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountWithoutTransactionsDto> getBankAccountsByOwnerId(@PathVariable("id") UUID bankAccountId) {
        return new ResponseEntity<>(bankAccountService.getBankAccountById(bankAccountId), HttpStatus.OK);
    }

    @Operation(
            summary = "Открыть банковский счёт.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/open")
    public ResponseEntity<BankAccountWithoutTransactionsDto> createBankAccount(@RequestBody @Valid CreateBankAccountDto createBankAccountDto) {
        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDto), HttpStatus.OK);
    }

    @Operation(
            summary = "Закрыть банковский счёт.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/close")
    public ResponseEntity<BankAccountWithoutTransactionsDto> closeBankAccount(@PathVariable("id") UUID bankAccountId) {
        return new ResponseEntity<>(bankAccountService.closeBankAccount(bankAccountId), HttpStatus.OK);
    }

    @Operation(
            summary = "Пополнить банковский счёт.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/deposit")
    public ResponseEntity<BankAccountDto> depositMoney(@PathVariable("id") UUID bankAccountId,
                                                       @RequestBody @Valid DepositMoneyDto depositMoneyDto) {
        return new ResponseEntity<>(bankAccountService.depositMoney(bankAccountId, depositMoneyDto.getAmount()), HttpStatus.OK);
    }

    @Operation(
            summary = "Снять деньги с банковского счёта.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<BankAccountDto> withdrawMoney(@PathVariable("id") UUID bankAccountId,
                                                        @RequestBody @Valid WithdrawMoneyDto withdrawMoneyDto) {
        return new ResponseEntity<>(bankAccountService.withdrawMoney(bankAccountId, withdrawMoneyDto.getAmount()), HttpStatus.OK);
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        return new ResponseEntity<>(jwtUtil.generateToken(UUID.fromString("77141e72-da79-44c8-b057-ea1ea39bac2a")), HttpStatus.OK);
    }

}
