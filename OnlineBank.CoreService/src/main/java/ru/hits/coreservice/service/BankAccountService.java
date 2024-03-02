package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.coreservice.dto.BankAccountDto;
import ru.hits.coreservice.dto.CreateBankAccountDto;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;
import ru.hits.coreservice.exception.ConflictException;
import ru.hits.coreservice.exception.ForbiddenException;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.repository.BankAccountRepository;
import ru.hits.coreservice.repository.TransactionRepository;
import ru.hits.coreservice.security.JwtUserData;

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

    public List<BankAccountDto> getAllBankAccounts(Sort.Direction creationDateSortDirection) {
        List<BankAccountEntity> bankAccounts = bankAccountRepository.findAll(Sort.by(creationDateSortDirection, "creationDate"));

        return bankAccounts.stream()
                .map(BankAccountDto::new)
                .collect(Collectors.toList());
    }

    public List<BankAccountDto> getBankAccountsByOwnerId(UUID ownerId, Sort.Direction creationDateSortDirection) {
        Sort sortByCreationDate = Sort.by(creationDateSortDirection, "creationDate");

        List<BankAccountEntity> bankAccounts = bankAccountRepository.findAllByOwnerId(ownerId, sortByCreationDate);

        return bankAccounts.stream()
                .map(BankAccountDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public BankAccountDto createBankAccount(CreateBankAccountDto createBankAccountDto) {
        BankAccountEntity bankAccount = BankAccountEntity.builder()
                .name(createBankAccountDto.getName())
                .number(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .ownerId(getAuthenticatedUserId())
                .isClosed(false)
                .creationDate(LocalDateTime.now())
                .transactions(Collections.emptyList())
                .build();

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    @Transactional
    public BankAccountDto closeBankAccount(UUID bankAccountId) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + bankAccountId + " уже закрыт");
        }

        bankAccount.setIsClosed(true);

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    @Transactional
    public BankAccountDto depositMoney(UUID bankAccountId, BigDecimal amount) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + bankAccountId + " закрыт");
        }

        BigDecimal newBalance = bankAccount.getBalance().add(amount);
        bankAccount.setBalance(newBalance);

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(amount)
                .transactionType(TransactionType.DEPOSIT)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);

        bankAccount.getTransactions().add(0, transaction);

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    @Transactional
    public BankAccountDto withdrawMoney(UUID bankAccountId, BigDecimal amount) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + bankAccountId + " закрыт");
        }

        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new ConflictException("Недостаточно средств на счете для снятия указанной суммы");
        }

        BigDecimal newBalance = bankAccount.getBalance().subtract(amount);
        bankAccount.setBalance(newBalance);

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(amount.negate())
                .transactionType(TransactionType.WITHDRAW)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);

        bankAccount.getTransactions().add(0, transaction);

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    /**
     * Метод для получения ID аутентифицированного пользователя.
     *
     * @return ID аутентифицированного пользователя.
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
