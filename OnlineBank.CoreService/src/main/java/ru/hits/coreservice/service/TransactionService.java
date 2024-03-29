package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.coreservice.dto.CreateTransactionMessage;
import ru.hits.coreservice.dto.PageInfoDto;
import ru.hits.coreservice.dto.TransactionDto;
import ru.hits.coreservice.dto.TransactionsWithPaginationDto;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;
import ru.hits.coreservice.exception.ConflictException;
import ru.hits.coreservice.exception.ForbiddenException;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.helpingservices.CheckPaginationInfoService;
import ru.hits.coreservice.repository.BankAccountRepository;
import ru.hits.coreservice.repository.TransactionRepository;
import ru.hits.coreservice.security.JwtUserData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final CheckPaginationInfoService checkPaginationInfoService;
    private final CoinGateCurrencyExchangeService currencyExchangeService;

    public TransactionsWithPaginationDto getTransactionsByBankAccountId(UUID bankAccountId, TransactionType transactionType, int pageNumber, int pageSize) {
        UUID authenticatedUserId = getAuthenticatedUserData().getId();
        List<String> authenticatedUserRoles = getAuthenticatedUserData().getRoles();

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId) && authenticatedUserRoles.size() == 1 && authenticatedUserRoles.contains("Customer")) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является" +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<TransactionEntity> transactionPage;
        if (transactionType != null) {
            transactionPage = transactionRepository.findAllByBankAccountAndTransactionTypeOrderByTransactionDateDesc(bankAccount, transactionType, pageable);
        } else {
            transactionPage = transactionRepository.findAllByBankAccountOrderByTransactionDateDesc(bankAccount, pageable);
        }

        List<TransactionDto> transactionDtos = transactionPage.getContent().stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());

        PageInfoDto pageInfo = new PageInfoDto(
                (int) transactionPage.getTotalElements(),
                pageNumber,
                Math.min(pageSize, transactionPage.getContent().size())
        );

        return new TransactionsWithPaginationDto(pageInfo, transactionDtos);
    }

    @Transactional
    public void createTransaction(CreateTransactionMessage createTransactionMessage) {
        if (TransactionType.fromString(createTransactionMessage.getTransactionType()) == TransactionType.WITHDRAW) {
            processWithdrawOrRepayLoan(createTransactionMessage, "Снятие средств");
        } else if (TransactionType.fromString(createTransactionMessage.getTransactionType()) == TransactionType.REPAY_LOAN) {
            processWithdrawOrRepayLoan(createTransactionMessage, "Платеж по кредиту");
            handleRepayLoanTransaction(createTransactionMessage);
        } else if (TransactionType.fromString(createTransactionMessage.getTransactionType()) == TransactionType.DEPOSIT) {
            processDepositOrTakeLoan(createTransactionMessage, "Пополнение счета");
        } else if (TransactionType.fromString(createTransactionMessage.getTransactionType()) == TransactionType.TAKE_LOAN) {
            processDepositOrTakeLoan(createTransactionMessage, "Взятие кредита");
            handleTakeLoanTransaction(createTransactionMessage);
        } else if (TransactionType.fromString(createTransactionMessage.getTransactionType()) == TransactionType.TRANSFER) {
            processTransfer(createTransactionMessage, "Перевод");
        }
    }

    @Transactional
    void processWithdrawOrRepayLoan(CreateTransactionMessage createTransactionMessage, String additionalInformation) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(createTransactionMessage.getBankAccountId())
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + createTransactionMessage.getFromBankAccountId() + " не найден"));

        String bankAccountCurrencyCode = bankAccount.getBalance().getCurrency().getCurrencyCode();
        BigDecimal invertedExchangeRate = currencyExchangeService.getExchangeRate(createTransactionMessage.getCurrencyCode(), bankAccountCurrencyCode);
        BigDecimal amountWithExchangeRate = createTransactionMessage.getAmount().multiply(invertedExchangeRate).negate();

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(amountWithExchangeRate)
                .transactionType(TransactionType.fromString(createTransactionMessage.getTransactionType()))
                .additionalInformation(additionalInformation)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);
        bankAccount.getTransactions().add(0, transaction);
        bankAccountRepository.save(bankAccount);

        sendTransactionUpdate(new TransactionDto(transaction));
    }

    @Transactional
    void processDepositOrTakeLoan(CreateTransactionMessage createTransactionMessage, String additionalInformation) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(createTransactionMessage.getBankAccountId())
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + createTransactionMessage.getBankAccountId() + " не найден"));

        String bankAccountCurrencyCode = bankAccount.getBalance().getCurrency().getCurrencyCode();
        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(createTransactionMessage.getCurrencyCode(), bankAccountCurrencyCode);
        BigDecimal amountWithExchangeRate = createTransactionMessage.getAmount().multiply(exchangeRateToTargetCurrency);

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(amountWithExchangeRate)
                .transactionType(TransactionType.fromString(createTransactionMessage.getTransactionType()))
                .additionalInformation(additionalInformation)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);
        bankAccount.getTransactions().add(0, transaction);
        bankAccountRepository.save(bankAccount);

        sendTransactionUpdate(new TransactionDto(transaction));
    }

    @Transactional
    void processTransfer(CreateTransactionMessage createTransactionMessage, String additionalInformation) {
        BankAccountEntity fromBankAccount = bankAccountRepository.findById(createTransactionMessage.getFromBankAccountId())
                .orElseThrow(() -> new NotFoundException("Банковский счет, с которого отправляются деньги, с ID " + createTransactionMessage.getFromBankAccountId() + " не найден"));
        BankAccountEntity toBankAccount = bankAccountRepository.findById(createTransactionMessage.getToBankAccountId())
                .orElseThrow(() -> new NotFoundException("Банковский счет, на который отправляются деньги, с ID " + createTransactionMessage.getToBankAccountId() + " не найден"));

        String fromCurrencyCode = fromBankAccount.getBalance().getCurrency().getCurrencyCode();
        String toCurrencyCode = toBankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(createTransactionMessage.getCurrencyCode(), toCurrencyCode);

        BigDecimal convertedAmount = createTransactionMessage.getAmount().multiply(exchangeRate);

        BigDecimal invertedExchangeRate = currencyExchangeService.getExchangeRate(createTransactionMessage.getCurrencyCode(), fromCurrencyCode);

        fromBankAccount.getBalance().setAmount(fromBankAccount.getBalance().getAmount().subtract(createTransactionMessage.getAmount().multiply(invertedExchangeRate)));
        toBankAccount.getBalance().setAmount(toBankAccount.getBalance().getAmount().add(convertedAmount));

        LocalDateTime transactionDate = LocalDateTime.now();

        TransactionEntity transactionFromBankAccount = TransactionEntity.builder()
                .transactionDate(transactionDate)
                .amount(createTransactionMessage.getAmount().multiply(invertedExchangeRate).negate())
                .transactionType(TransactionType.TRANSFER)
                .additionalInformation(additionalInformation)
                .bankAccount(fromBankAccount)
                .build();

        TransactionEntity transactionToBankAccount = TransactionEntity.builder()
                .transactionDate(transactionDate)
                .amount(convertedAmount)
                .transactionType(TransactionType.TRANSFER)
                .additionalInformation(additionalInformation)
                .bankAccount(toBankAccount)
                .build();

        transactionFromBankAccount = transactionRepository.save(transactionFromBankAccount);
        transactionToBankAccount = transactionRepository.save(transactionToBankAccount);

        fromBankAccount.getTransactions().add(0, transactionFromBankAccount);
        toBankAccount.getTransactions().add(0, transactionToBankAccount);

        bankAccountRepository.save(fromBankAccount);
        bankAccountRepository.save(toBankAccount);

        sendTransactionUpdate(new TransactionDto(transactionFromBankAccount));
        sendTransactionUpdate(new TransactionDto(transactionToBankAccount));
    }

    @Transactional
    void handleTakeLoanTransaction(CreateTransactionMessage createTransactionMessage) {
        BankAccountEntity masterBankAccount = bankAccountRepository.findById(UUID.fromString("cb1ef860-9f51-4e49-8e7d-f6694b10fc99"))
                .orElseThrow(() -> new NotFoundException("Мастер-счет не найден"));

        String masterBankAccountCurrencyCode = masterBankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(masterBankAccountCurrencyCode, createTransactionMessage.getCurrencyCode());
        BigDecimal masterBankAccountBalanceWithExchangeRate = masterBankAccount.getBalance().getAmount().multiply(exchangeRateToTargetCurrency);

        if (masterBankAccountBalanceWithExchangeRate.compareTo(createTransactionMessage.getAmount()) < 0) {
            throw new ConflictException("Недостаточно средств на мастер-счете для списания указанной суммы");
        }

        BigDecimal invertedExchangeRate = currencyExchangeService.getExchangeRate(createTransactionMessage.getCurrencyCode(), masterBankAccountCurrencyCode);

        masterBankAccount.getBalance().setAmount(masterBankAccount.getBalance().getAmount().subtract(createTransactionMessage.getAmount().multiply(invertedExchangeRate)));

        TransactionEntity masterBankAccountTransaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(createTransactionMessage.getAmount().multiply(invertedExchangeRate).negate())
                .transactionType(TransactionType.fromString(createTransactionMessage.getTransactionType()))
                .additionalInformation("Снятие средств")
                .bankAccount(masterBankAccount)
                .build();

        masterBankAccountTransaction = transactionRepository.save(masterBankAccountTransaction);

        masterBankAccount.getTransactions().add(0, masterBankAccountTransaction);

        bankAccountRepository.save(masterBankAccount);

        sendTransactionUpdate(new TransactionDto(masterBankAccountTransaction));
    }

    @Transactional
    void handleRepayLoanTransaction(CreateTransactionMessage createTransactionMessage) {
        BankAccountEntity masterBankAccount = bankAccountRepository.findById(UUID.fromString("cb1ef860-9f51-4e49-8e7d-f6694b10fc99"))
                .orElseThrow(() -> new NotFoundException("Мастер-счет не найден"));

        String masterBankAccountCurrencyCode = masterBankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(createTransactionMessage.getCurrencyCode(), masterBankAccountCurrencyCode);
        BigDecimal amountWithExchangeRate = createTransactionMessage.getAmount().multiply(exchangeRateToTargetCurrency);

        BigDecimal newMasterBankAccountBalance = masterBankAccount.getBalance().getAmount().add(amountWithExchangeRate);
        masterBankAccount.getBalance().setAmount(newMasterBankAccountBalance);

        TransactionEntity masterBankAccountTransaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(amountWithExchangeRate)
                .transactionType(TransactionType.fromString(createTransactionMessage.getTransactionType()))
                .additionalInformation("Пополнение счета")
                .bankAccount(masterBankAccount)
                .build();

        masterBankAccountTransaction = transactionRepository.save(masterBankAccountTransaction);

        masterBankAccount.getTransactions().add(0, masterBankAccountTransaction);

        bankAccountRepository.save(masterBankAccount);

        sendTransactionUpdate(new TransactionDto(masterBankAccountTransaction));
    }

    private void sendTransactionUpdate(TransactionDto transaction) {
        String destination = "/topic/bank-accounts/" + transaction.getBankAccountId() + "/transactions";
        messagingTemplate.convertAndSend(destination, transaction);
    }

    private JwtUserData getAuthenticatedUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData;
    }

}
