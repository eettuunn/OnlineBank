package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.repository.BankAccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationBankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public Boolean checkBankAccountExistenceById(UUID bankAccountId) {
        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElse(null);

        if (bankAccount == null) {
            return false;
        }

        return !bankAccount.getIsClosed();
    }

}
