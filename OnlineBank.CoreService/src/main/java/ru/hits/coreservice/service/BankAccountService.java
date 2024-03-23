package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.coreservice.dto.*;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.Money;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {

    private final SimpMessagingTemplate messagingTemplate;

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final CheckPaginationInfoService checkPaginationInfoService;
    private final IntegrationRequestsService integrationRequestsService;
    private final CoinGateCurrencyExchangeService currencyExchangeService;


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

        PageInfoDto pageInfo = new PageInfoDto(
                (int) bankAccountPage.getTotalElements(),
                pageNumber,
                Math.min(pageSize, bankAccountPage.getContent().size())
        );

        return new BankAccountsWithPaginationDto(pageInfo, bankAccountDtos);
    }

    public BankAccountsWithPaginationDto getBankAccountsByOwnerId(UUID ownerId, Sort.Direction creationDateSortDirection, Boolean isClosed, int pageNumber, int pageSize) {
        checkPaginationInfoService.checkPagination(pageNumber, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(creationDateSortDirection, "creationDate"));

        Page<BankAccountEntity> bankAccountPage;
        if (isClosed != null) {
            bankAccountPage = bankAccountRepository.findAllByOwnerIdAndIsClosed(ownerId, isClosed, pageable);
        } else {
            bankAccountPage = bankAccountRepository.findAllByOwnerId(ownerId, pageable);
        }

        List<BankAccountWithoutTransactionsDto> bankAccountDtos = bankAccountPage.getContent().stream()
                .map(BankAccountWithoutTransactionsDto::new)
                .collect(Collectors.toList());

        PageInfoDto pageInfo = new PageInfoDto(
                (int) bankAccountPage.getTotalElements(),
                pageNumber,
                Math.min(pageSize, bankAccountPage.getContent().size())
        );

        return new BankAccountsWithPaginationDto(pageInfo, bankAccountDtos);
    }

    public BankAccountWithoutTransactionsDto getBankAccountById(UUID bankAccountId) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        return new BankAccountWithoutTransactionsDto(bankAccount);
    }

    @Transactional
    public BankAccountWithoutTransactionsDto createBankAccount(CreateBankAccountDto createBankAccountDto) {
        if (!integrationRequestsService.checkUserExistence(createBankAccountDto.getUserId())) {
            throw new NotFoundException("Пользователя с ID " + createBankAccountDto.getUserId() + " не существует");
        }

        Currency currency = Currency.getInstance(createBankAccountDto.getCurrencyCode());

        BankAccountEntity bankAccount = BankAccountEntity.builder()
                .name(createBankAccountDto.getName())
                .number(generateAccountNumber())
                .balance(new Money(BigDecimal.ZERO, currency))
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
        if (!integrationRequestsService.checkUserExistence(closeBankAccountDto.getUserId())) {
            throw new NotFoundException("Пользователя с ID " + closeBankAccountDto.getUserId() + " не существует");
        }

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
        if (!integrationRequestsService.checkUserExistence(depositMoneyDto.getUserId())) {
            throw new NotFoundException("Пользователя с ID " + depositMoneyDto.getUserId() + " не существует");
        }

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

        String additionalInformation = null;
        if (TransactionType.fromDepositTransactionType(depositMoneyDto.getTransactionType()) == TransactionType.DEPOSIT) {
            additionalInformation = "Пополнение счета";
        } else if (TransactionType.fromDepositTransactionType(depositMoneyDto.getTransactionType()) == TransactionType.TAKE_LOAN) {
            additionalInformation = "Взятие кредита";
        }

        String bankAccountCurrencyCode = bankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(depositMoneyDto.getCurrencyCode(), bankAccountCurrencyCode);
        BigDecimal amountWithExchangeRate = depositMoneyDto.getAmount().multiply(exchangeRateToTargetCurrency);

        BigDecimal newBalance = bankAccount.getBalance().getAmount().add(amountWithExchangeRate);
        bankAccount.getBalance().setAmount(newBalance);

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(amountWithExchangeRate)
                .transactionType(TransactionType.fromDepositTransactionType(depositMoneyDto.getTransactionType()))
                .additionalInformation(additionalInformation)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);

        bankAccount.getTransactions().add(0, transaction);

        bankAccount = bankAccountRepository.save(bankAccount);

        sendTransactionUpdate(new TransactionDto(transaction));

        return new BankAccountDto(bankAccount);
    }

    @Transactional
    public BankAccountDto transferMoney(UUID toBankAccountId, TransferMoneyDto transferMoneyDto) {
        if (!integrationRequestsService.checkUserExistence(transferMoneyDto.getUserId())) {
            throw new NotFoundException("Пользователя с ID " + transferMoneyDto.getUserId() + " не существует");
        }

        UUID authenticatedUserId = transferMoneyDto.getUserId();

        BankAccountEntity fromBankAccount = bankAccountRepository.findById(transferMoneyDto.getFromAccountId())
                .orElseThrow(() -> new NotFoundException("Банковский счет, с которого отправляются деньги, с ID " + transferMoneyDto.getFromAccountId() + " не найден"));
        BankAccountEntity toBankAccount = bankAccountRepository.findById(toBankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет, на который отправляются деньги, с ID " + toBankAccountId + " не найден"));

        if (!fromBankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + fromBankAccount.getId());
        }

        if (Boolean.TRUE.equals(fromBankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет, с которого отправляются деньги, с ID " + fromBankAccount.getId() + " закрыт");
        }

        if (Boolean.TRUE.equals(toBankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет, на который отправляются деньги, с ID " + toBankAccountId + " закрыт");
        }

        String fromCurrencyCode = fromBankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(fromCurrencyCode, transferMoneyDto.getCurrencyCode());
        BigDecimal balanceWithExchangeRate = fromBankAccount.getBalance().getAmount().multiply(exchangeRateToTargetCurrency);

        if (balanceWithExchangeRate.compareTo(transferMoneyDto.getAmount()) < 0) {
            throw new ConflictException("Недостаточно средств на счете для перевода указанной суммы");
        }

        String toCurrencyCode = toBankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(transferMoneyDto.getCurrencyCode(), toCurrencyCode);

        BigDecimal convertedAmount = transferMoneyDto.getAmount().multiply(exchangeRate);

        BigDecimal invertedExchangeRate = currencyExchangeService.getExchangeRate(transferMoneyDto.getCurrencyCode(), fromCurrencyCode);

        fromBankAccount.getBalance().setAmount(fromBankAccount.getBalance().getAmount().subtract(transferMoneyDto.getAmount().multiply(invertedExchangeRate)));
        toBankAccount.getBalance().setAmount(toBankAccount.getBalance().getAmount().add(convertedAmount));

        LocalDateTime transactionDate = LocalDateTime.now();

        TransactionEntity transactionFromBankAccount = TransactionEntity.builder()
                .transactionDate(transactionDate)
                .amount(transferMoneyDto.getAmount().multiply(invertedExchangeRate).negate())
                .transactionType(TransactionType.TRANSFER)
                .additionalInformation("Перевод")
                .bankAccount(fromBankAccount)
                .build();

        TransactionEntity transactionToBankAccount = TransactionEntity.builder()
                .transactionDate(transactionDate)
                .amount(convertedAmount)
                .transactionType(TransactionType.TRANSFER)
                .additionalInformation("Перевод")
                .bankAccount(toBankAccount)
                .build();

        transactionFromBankAccount = transactionRepository.save(transactionFromBankAccount);
        transactionToBankAccount = transactionRepository.save(transactionToBankAccount);

        fromBankAccount.getTransactions().add(0, transactionFromBankAccount);
        toBankAccount.getTransactions().add(0, transactionToBankAccount);

        fromBankAccount = bankAccountRepository.save(fromBankAccount);
        bankAccountRepository.save(toBankAccount);

        sendTransactionUpdate(new TransactionDto(transactionFromBankAccount));
        sendTransactionUpdate(new TransactionDto(transactionToBankAccount));

        return new BankAccountDto(fromBankAccount);
    }

    @Transactional
    public BankAccountDto withdrawMoney(UUID bankAccountId, WithdrawMoneyDto withdrawMoneyDto) {
        if (!integrationRequestsService.checkUserExistence(withdrawMoneyDto.getUserId())) {
            throw new NotFoundException("Пользователя с ID " + withdrawMoneyDto.getUserId() + " не существует");
        }

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

        String additionalInformation = null;
        if (TransactionType.fromWithdrawTransactionType(withdrawMoneyDto.getTransactionType()) == TransactionType.WITHDRAW) {
            additionalInformation = "Снятие средств";
        } else if (TransactionType.fromWithdrawTransactionType(withdrawMoneyDto.getTransactionType()) == TransactionType.REPAY_LOAN) {
            additionalInformation = "Платеж по кредиту";
        }

        String bankAccountCurrencyCode = bankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(bankAccountCurrencyCode, withdrawMoneyDto.getCurrencyCode());
        BigDecimal balanceWithExchangeRate = bankAccount.getBalance().getAmount().multiply(exchangeRateToTargetCurrency);

        if (balanceWithExchangeRate.compareTo(withdrawMoneyDto.getAmount()) < 0) {
            throw new ConflictException("Недостаточно средств на счете для перевода указанной суммы");
        }

        BigDecimal invertedExchangeRate = currencyExchangeService.getExchangeRate(withdrawMoneyDto.getCurrencyCode(), bankAccountCurrencyCode);

        bankAccount.getBalance().setAmount(bankAccount.getBalance().getAmount().subtract(withdrawMoneyDto.getAmount().multiply(invertedExchangeRate)));

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionDate(LocalDateTime.now())
                .amount(withdrawMoneyDto.getAmount().multiply(invertedExchangeRate).negate())
                .transactionType(TransactionType.fromWithdrawTransactionType(withdrawMoneyDto.getTransactionType()))
                .additionalInformation(additionalInformation)
                .bankAccount(bankAccount)
                .build();

        transaction = transactionRepository.save(transaction);

        bankAccount.getTransactions().add(0, transaction);

        bankAccount = bankAccountRepository.save(bankAccount);

        sendTransactionUpdate(new TransactionDto(transaction));

        return new BankAccountDto(bankAccount);
    }

    public BankAccountWithoutTransactionsDto updateBankAccountName(UUID bankAccountId,
                                                                   UpdateBankAccountNameDto updateBankAccountNameDto) {
        if (!integrationRequestsService.checkUserExistence(updateBankAccountNameDto.getUserId())) {
            throw new NotFoundException("Пользователя с ID " + updateBankAccountNameDto.getUserId() + " не существует");
        }

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

    public Boolean checkBankAccountExistenceById(UUID bankAccountId) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElse(null);

        if (bankAccount == null) {
            return false;
        }

        return !bankAccount.getIsClosed();
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    private void sendTransactionUpdate(TransactionDto transaction) {
        String destination = "/topic/bank-accounts/" + transaction.getBankAccountId() + "/transactions";
        messagingTemplate.convertAndSend(destination, transaction);
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
