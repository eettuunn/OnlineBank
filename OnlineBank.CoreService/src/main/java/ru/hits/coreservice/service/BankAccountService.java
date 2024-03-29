package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.coreservice.dto.*;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.Money;
import ru.hits.coreservice.enumeration.TransactionType;
import ru.hits.coreservice.exception.ConflictException;
import ru.hits.coreservice.exception.ForbiddenException;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.helpingservices.CheckPaginationInfoService;
import ru.hits.coreservice.repository.BankAccountRepository;
import ru.hits.coreservice.repository.TransactionRepository;
import ru.hits.coreservice.security.JwtUserData;
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

        List<String> authenticatedUserRoles = getAuthenticatedUserData().getRoles();
        if (authenticatedUserRoles.size() == 1 && authenticatedUserRoles.contains("Customer")) {
            throw new ForbiddenException("Клиент не может просматривать список всех счетов");
        }

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

        UUID authenticatedUserId = getAuthenticatedUserData().getId();
        List<String> authenticatedUserRoles = getAuthenticatedUserData().getRoles();
        if (authenticatedUserId != ownerId && authenticatedUserRoles.size() == 1 && authenticatedUserRoles.contains("Customer")) {
            throw new ForbiddenException("Клиент не может просматривать список счетов другого человека");
        }

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

        UUID authenticatedUserId = getAuthenticatedUserData().getId();
        List<String> authenticatedUserRoles = getAuthenticatedUserData().getRoles();
        if (!bankAccount.getOwnerId().equals(authenticatedUserId) && authenticatedUserRoles.size() == 1 && authenticatedUserRoles.contains("Customer")) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является" +
                    " владельцем банковского счета с ID " + bankAccountId);
        }

        return new BankAccountWithoutTransactionsDto(bankAccount);
    }

    @Transactional
    public BankAccountWithoutTransactionsDto createBankAccount(CreateBankAccountDto createBankAccountDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (!integrationRequestsService.checkUserExistence(authenticatedUserId)) {
            throw new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует");
        }

        Currency currency = Currency.getInstance(createBankAccountDto.getCurrencyCode());

        BankAccountEntity bankAccount = BankAccountEntity.builder()
                .name(createBankAccountDto.getName())
                .number(generateAccountNumber())
                .balance(new Money(BigDecimal.ZERO, currency))
                .ownerId(authenticatedUserId)
                .isClosed(false)
                .creationDate(LocalDateTime.now())
                .transactions(Collections.emptyList())
                .build();

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountWithoutTransactionsDto(bankAccount);
    }

    @Transactional
    public BankAccountWithoutTransactionsDto closeBankAccount(UUID bankAccountId) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (!integrationRequestsService.checkUserExistence(authenticatedUserId)) {
            throw new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует");
        }

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является" +
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
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (!integrationRequestsService.checkUserExistence(authenticatedUserId)) {
            throw new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует");
        }

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является" +
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

            BankAccountEntity masterBankAccount = bankAccountRepository.findById(UUID.fromString("cb1ef860-9f51-4e49-8e7d-f6694b10fc99"))
                    .orElseThrow(() -> new NotFoundException("Мастер-счет не найден"));

            String masterBankAccountCurrencyCode = masterBankAccount.getBalance().getCurrency().getCurrencyCode();

            BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(masterBankAccountCurrencyCode, depositMoneyDto.getCurrencyCode());
            BigDecimal masterBankAccountBalanceWithExchangeRate = masterBankAccount.getBalance().getAmount().multiply(exchangeRateToTargetCurrency);

            if (masterBankAccountBalanceWithExchangeRate.compareTo(depositMoneyDto.getAmount()) < 0) {
                throw new ConflictException("Недостаточно средств на мастер-счете для списания указанной суммы");
            }

            BigDecimal invertedExchangeRate = currencyExchangeService.getExchangeRate(depositMoneyDto.getCurrencyCode(), masterBankAccountCurrencyCode);

            masterBankAccount.getBalance().setAmount(masterBankAccount.getBalance().getAmount().subtract(depositMoneyDto.getAmount().multiply(invertedExchangeRate)));

            bankAccountRepository.save(masterBankAccount);
        }

        String bankAccountCurrencyCode = bankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(depositMoneyDto.getCurrencyCode(), bankAccountCurrencyCode);
        BigDecimal amountWithExchangeRate = depositMoneyDto.getAmount().multiply(exchangeRateToTargetCurrency);

        BigDecimal newBalance = bankAccount.getBalance().getAmount().add(amountWithExchangeRate);
        bankAccount.getBalance().setAmount(newBalance);
        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    @Transactional
    public BankAccountDto transferMoney(UUID toBankAccountId, TransferMoneyDto transferMoneyDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (!integrationRequestsService.checkUserExistence(authenticatedUserId)) {
            throw new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует");
        }

        BankAccountEntity fromBankAccount = bankAccountRepository.findById(transferMoneyDto.getFromAccountId())
                .orElseThrow(() -> new NotFoundException("Банковский счет, с которого отправляются деньги, с ID " + transferMoneyDto.getFromAccountId() + " не найден"));
        BankAccountEntity toBankAccount = bankAccountRepository.findById(toBankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет, на который отправляются деньги, с ID " + toBankAccountId + " не найден"));

        if (!fromBankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является" +
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

        fromBankAccount = bankAccountRepository.save(fromBankAccount);
        bankAccountRepository.save(toBankAccount);

        return new BankAccountDto(fromBankAccount);
    }

    @Transactional
    public BankAccountDto withdrawMoney(UUID bankAccountId, WithdrawMoneyDto withdrawMoneyDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (!integrationRequestsService.checkUserExistence(authenticatedUserId)) {
            throw new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует");
        }

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является" +
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

            BankAccountEntity masterBankAccount = bankAccountRepository.findById(UUID.fromString("cb1ef860-9f51-4e49-8e7d-f6694b10fc99"))
                    .orElseThrow(() -> new NotFoundException("Мастер-счет не найден"));

            String masterBankAccountCurrencyCode = masterBankAccount.getBalance().getCurrency().getCurrencyCode();

            BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(withdrawMoneyDto.getCurrencyCode(), masterBankAccountCurrencyCode);
            BigDecimal amountWithExchangeRate = withdrawMoneyDto.getAmount().multiply(exchangeRateToTargetCurrency);

            BigDecimal newMasterBankAccountBalance = masterBankAccount.getBalance().getAmount().add(amountWithExchangeRate);
            masterBankAccount.getBalance().setAmount(newMasterBankAccountBalance);
            bankAccountRepository.save(masterBankAccount);
        }

        String bankAccountCurrencyCode = bankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(bankAccountCurrencyCode, withdrawMoneyDto.getCurrencyCode());
        BigDecimal balanceWithExchangeRate = bankAccount.getBalance().getAmount().multiply(exchangeRateToTargetCurrency);

        if (balanceWithExchangeRate.compareTo(withdrawMoneyDto.getAmount()) < 0) {
            throw new ConflictException("Недостаточно средств на счете для перевода указанной суммы");
        }

        BigDecimal invertedExchangeRate = currencyExchangeService.getExchangeRate(withdrawMoneyDto.getCurrencyCode(), bankAccountCurrencyCode);
        bankAccount.getBalance().setAmount(bankAccount.getBalance().getAmount().subtract(withdrawMoneyDto.getAmount().multiply(invertedExchangeRate)));
        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    public BankAccountWithoutTransactionsDto updateBankAccountName(UUID bankAccountId,
                                                                   UpdateBankAccountNameDto updateBankAccountNameDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        if (!integrationRequestsService.checkUserExistence(authenticatedUserId)) {
            throw new NotFoundException("Пользователя с ID " + authenticatedUserId + " не существует");
        }

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является" +
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

    public Boolean  checkBankAccountAmountOfMoney(UUID bankAccountId, CheckMoneyDto checkMoneyDto) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        String bankAccountCurrencyCode = bankAccount.getBalance().getCurrency().getCurrencyCode();

        BigDecimal exchangeRateToTargetCurrency = currencyExchangeService.getExchangeRate(bankAccountCurrencyCode, checkMoneyDto.getCurrencyCode());
        BigDecimal balanceWithExchangeRate = bankAccount.getBalance().getAmount().multiply(exchangeRateToTargetCurrency);

        if (balanceWithExchangeRate.compareTo(checkMoneyDto.getAmount()) < 0) {
            return false;
        }

        return true;
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

    private JwtUserData getAuthenticatedUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData;
    }

}
