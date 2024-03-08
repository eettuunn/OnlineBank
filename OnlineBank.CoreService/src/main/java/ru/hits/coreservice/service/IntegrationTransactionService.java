package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.coreservice.dto.CreateTransactionDto;
import ru.hits.coreservice.dto.TransactionDto;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;
import ru.hits.coreservice.exception.BadRequestException;
import ru.hits.coreservice.exception.ConflictException;
import ru.hits.coreservice.exception.ForbiddenException;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.repository.BankAccountRepository;
import ru.hits.coreservice.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationTransactionService {

    private final BankAccountRepository bankAccountRepository;

    private final TransactionRepository transactionRepository;

    public TransactionDto createTransaction(CreateTransactionDto createTransactionDto) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(createTransactionDto.getBankAccountId())
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + createTransactionDto.getBankAccountId() + " не найден"));

        UUID authenticatedUserId = createTransactionDto.getUserId();

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    "владельцем банковского счета с ID " + bankAccount.getId());
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + bankAccount.getId() + " закрыт");
        }

        if (TransactionType.fromLoanTransactionType(createTransactionDto.getTransactionType()) == TransactionType.TAKE_LOAN && (createTransactionDto.getAmount() == null || createTransactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BadRequestException("Для операции TAKE_LOAN (взять кредит) поле amount должно быть положительным и не равным нулю");
        } else if (TransactionType.fromLoanTransactionType(createTransactionDto.getTransactionType()) == TransactionType.REPAY_LOAN && (createTransactionDto.getAmount() == null || createTransactionDto.getAmount().compareTo(BigDecimal.ZERO) >= 0)) {
            throw new BadRequestException("Для операции REPAY_LOAN (погасить кредит) поле amount должно быть отрицательным и не равным нулю");
        }

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionDate(LocalDateTime.now());
        transactionEntity.setAmount(createTransactionDto.getAmount());
        transactionEntity.setAdditionalInformation(createTransactionDto.getAdditionalInformation());
        transactionEntity.setTransactionType(TransactionType.fromLoanTransactionType(createTransactionDto.getTransactionType()));
        transactionEntity.setBankAccount(bankAccount);

        TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);

        return new TransactionDto(savedTransaction);
    }

}
