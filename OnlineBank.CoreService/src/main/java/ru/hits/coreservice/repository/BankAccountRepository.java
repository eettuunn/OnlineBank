package ru.hits.coreservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.coreservice.entity.BankAccountEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

    Page<BankAccountEntity> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Page<BankAccountEntity> findAllByOwnerIdAndIsClosed(UUID ownerId, Boolean isClosed, Pageable pageable);

    Page<BankAccountEntity> findAllByIsClosed(Boolean isClosed, Pageable pageable);

}
