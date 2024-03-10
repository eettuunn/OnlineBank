package ru.hits.coreservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.coreservice.entity.BankAccountEntity;
import ru.hits.coreservice.entity.TransactionEntity;
import ru.hits.coreservice.enumeration.TransactionType;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    Page<TransactionEntity> findAllByBankAccountOrderByTransactionDateDesc(BankAccountEntity bankAccount, Pageable pageable);

    Page<TransactionEntity> findAllByBankAccountAndTransactionTypeOrderByTransactionDateDesc(BankAccountEntity bankAccount, TransactionType transactionType, Pageable pageable);

}
