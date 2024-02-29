package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.coreservice.dto.BankAccountDto;
import ru.hits.coreservice.dto.CreateBankAccountDto;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.repository.BankAccountRepository;

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
        BankAccountEntity bankAccount = BankAccountEntity.builder()
                .name(createBankAccountDto.getName())
                .number(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .ownerId(UUID.fromString("77141e72-da79-44c8-b057-ea1ea39bac2a"))
                .isClosed(false)
                .transactions(Collections.emptyList())
                .build();

        bankAccount = bankAccountRepository.save(bankAccount);

        return new BankAccountDto(bankAccount);
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // Генерируем 12 цифр
        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10)); // Добавляем случайную цифру от 0 до 9
        }

        return sb.toString();
    }

}
