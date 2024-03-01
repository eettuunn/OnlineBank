package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.coreservice.dto.BankAccountDto;
import ru.hits.coreservice.dto.CreateBankAccountDto;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.exception.ConflictException;
import ru.hits.coreservice.exception.ForbiddenException;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.repository.BankAccountRepository;
import ru.hits.coreservice.security.JwtUserData;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public BankAccountDto createBankAccount(CreateBankAccountDto createBankAccountDto) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        BankAccountEntity bankAccount = BankAccountEntity.builder()
                .name(createBankAccountDto.getName())
                .number(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .ownerId(authenticatedUserId)
                .isClosed(false)
                .transactions(Collections.emptyList())
                .build();

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    @Transactional
    public BankAccountDto closeBankAccount(UUID id) {
        UUID authenticatedUserId = getAuthenticatedUserId();

        BankAccountEntity bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + id + " не найден"));

        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
                    " владельцем банковского счета с ID " + id);
        }

        if (Boolean.TRUE.equals(bankAccount.getIsClosed())) {
            throw new ConflictException("Банковский счет с ID " + id + " уже закрыт");
        }

        bankAccount.setIsClosed(true);

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
