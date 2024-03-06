package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.coreservice.dto.*;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;
import ru.hits.coreservice.exception.ConflictException;
import ru.hits.coreservice.exception.ForbiddenException;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.helpingservices.CheckPaginationInfoService;
import ru.hits.coreservice.repository.BankAccountRepository;
import ru.hits.coreservice.repository.TransactionRepository;
//import ru.hits.coreservice.security.JwtUserData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final CheckPaginationInfoService checkPaginationInfoService;


    public BankAccountsWithPaginationDto getAllBankAccounts(Sort.Direction creationDateSortDirection, Boolean isClosed, int pageNumber, int pageSize) {
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(creationDateSortDirection, "creationDate"));

        Page<BankAccountEntity> bankAccountPage;
        if (isClosed != null) {
            bankAccountPage = bankAccountRepository.findAllByIsClosed(isClosed, pageable);
        } else {
            bankAccountPage = bankAccountRepository.findAll(pageable);
        }

        List<BankAccountWithoutTransactionsDto> bankAccountDtos = bankAccountPage.getContent().stream()
                .map(BankAccountWithoutTransactionsDto::new)
                .collect(Collectors.toList());

        return new BankAccountsWithPaginationDto(
                new PageInfoDto(pageNumber, pageSize, bankAccountDtos.size()),
                bankAccountDtos
        );
    }

    public BankAccountsWithPaginationDto getBankAccountsByOwnerId(UUID ownerId, Sort.Direction creationDateSortDirection, Boolean isClosed, int pageNumber, int pageSize) {
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(creationDateSortDirection, "creationDate"));

        List<BankAccountEntity> bankAccounts;
        if (isClosed != null) {
            bankAccounts = bankAccountRepository.findAllByOwnerIdAndIsClosed(ownerId, isClosed, pageable);
        } else {
            bankAccounts = bankAccountRepository.findAllByOwnerId(ownerId, pageable);
        }

        List<BankAccountWithoutTransactionsDto> bankAccountDtos = bankAccounts.stream()
                .map(BankAccountWithoutTransactionsDto::new)
                .collect(Collectors.toList());

        return new BankAccountsWithPaginationDto(
                new PageInfoDto(pageNumber, pageSize, bankAccountDtos.size()),
                bankAccountDtos
        );
    }

    public BankAccountWithoutTransactionsDto getBankAccountById(UUID bankAccountId) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        return new BankAccountWithoutTransactionsDto(bankAccount);
    }

    @Transactional
    public BankAccountWithoutTransactionsDto createBankAccount(CreateBankAccountDto createBankAccountDto) {
        BankAccountEntity bankAccount = BankAccountEntity.builder()
                .name(createBankAccountDto.getName())
                .number(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .ownerId(createBankAccountDto.getUserId())
                .isClosed(false)
                .creationDate(LocalDateTime.now())
                .transactions(Collections.emptyList())
                .build();

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountWithoutTransactionsDto(bankAccount);
    }

    @Transactional
    public BankAccountWithoutTransactionsDto closeBankAccount(UUID bankAccountId, CloseBankAccountDto closeBankAccountDto) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(closeBankAccountDto.getUserId())) {
            throw new ForbiddenException("Пользователь с ID " + closeBankAccountDto.getUserId() + " не является " +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + bankAccountId + " уже закрыт");
        }

        bankAccount.setIsClosed(true);

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountWithoutTransactionsDto(bankAccount);
    }

    @Transactional
    public BankAccountDto depositMoney(UUID bankAccountId, DepositMoneyDto depositMoneyDto) {
        UUID authenticatedUserId = depositMoneyDto.getUserId();

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + bankAccountId + " закрыт");
        }

        BigDecimal newBalance = bankAccount.getBalance().add(depositMoneyDto.getAmount());
        bankAccount.setBalance(newBalance);

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(depositMoneyDto.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);

        bankAccount.getTransactions().add(0, transaction);

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    @Transactional
    public BankAccountDto withdrawMoney(UUID bankAccountId, WithdrawMoneyDto withdrawMoneyDto) {
        UUID authenticatedUserId = withdrawMoneyDto.getUserId();

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + bankAccountId + " закрыт");
        }

        if (bankAccount.getBalance().compareTo(withdrawMoneyDto.getAmount()) < 0) {
            throw new ConflictException("Недостаточно средств на счете для снятия указанной суммы");
        }

        BigDecimal newBalance = bankAccount.getBalance().subtract(withdrawMoneyDto.getAmount());
        bankAccount.setBalance(newBalance);

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(withdrawMoneyDto.getAmount().negate())
                .transactionType(TransactionType.WITHDRAW)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);

        bankAccount.getTransactions().add(0, transaction);

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    public BankAccountWithoutTransactionsDto updateBankAccountName(UUID bankAccountId,
                                                                   UpdateBankAccountNameDto updateBankAccountNameDto) {
        UUID authenticatedUserId = updateBankAccountNameDto.getUserId();

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        bankAccount.setName(updateBankAccountNameDto.getName());
        BankAccountEntity updatedBankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountWithoutTransactionsDto(updatedBankAccount);
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

//    /**
//     * Метод для получения ID аутентифицированного пользователя.
//     *
//     * @return ID аутентифицированного пользователя.
//     */
//    private UUID getAuthenticatedUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
//        return userData.getId();
//    }

}
