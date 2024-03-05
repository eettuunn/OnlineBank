package ru.hits.coreservice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.coreservice.entity.BankAccountEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

    List<BankAccountEntity> findAllByOwnerId(UUID ownerId, Sort creationDateSortDirection);

}
