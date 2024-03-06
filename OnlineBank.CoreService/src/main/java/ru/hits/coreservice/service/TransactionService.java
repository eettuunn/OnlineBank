package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.coreservice.dto.TransactionDto;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.repository.BankAccountRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountRepository bankAccountRepository;

    public List<TransactionDto> getTransactionsByBankAccountId(UUID bankAccountId, TransactionType transactionType) {
//        UUID authenticatedUserId = UUID.fromString("77141e72-da79-44c8-b057-ea1ea39bac2a");

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        //TODO: потом раскомментить, когда в токене появятся роли
//        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
//            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
//                    " владельцем банковского счета с ID " + bankAccountId);
//        }

        Stream<TransactionEntity> transactionStream = bankAccount.getTransactions().stream();

        if (transactionType != null) {
            transactionStream = transactionStream.filter(transaction -> transaction.getTransactionType() == transactionType);
        }

        return transactionStream
                .map(TransactionDto::new)
                .collect(Collectors.toList());
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
