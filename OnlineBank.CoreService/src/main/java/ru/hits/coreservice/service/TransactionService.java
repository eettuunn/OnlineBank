package ru.hits.coreservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.hits.coreservice.dto.PageInfoDto;
import ru.hits.coreservice.dto.TransactionDto;
import ru.hits.coreservice.dto.TransactionsWithPaginationDto;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;
import ru.hits.coreservice.exception.NotFoundException;
import ru.hits.coreservice.helpingservices.CheckPaginationInfoService;
import ru.hits.coreservice.repository.BankAccountRepository;
import ru.hits.coreservice.repository.TransactionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountRepository bankAccountRepository;

    private final TransactionRepository transactionRepository;

    private final CheckPaginationInfoService checkPaginationInfoService;

    public TransactionsWithPaginationDto getTransactionsByBankAccountId(UUID bankAccountId, TransactionType transactionType, int pageNumber, int pageSize) {
//        UUID authenticatedUserId = UUID.fromString("77141e72-da79-44c8-b057-ea1ea39bac2a");

        BankAccountEntity bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new NotFoundException("Банковский счет с ID " + bankAccountId + " не найден"));

        //TODO: потом раскомментить, когда в токене появятся роли
//        if (!bankAccount.getOwnerId().equals(authenticatedUserId)) {
//            throw new ForbiddenException("Пользователь с ID " + authenticatedUserId + " не является " +
//                    " владельцем банковского счета с ID " + bankAccountId);
//        }

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
                transactionPage.getTotalPages(),
                pageNumber,
                Math.min(pageSize, transactionPage.getContent().size())
        );

        return new TransactionsWithPaginationDto(pageInfo, transactionDtos);
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
