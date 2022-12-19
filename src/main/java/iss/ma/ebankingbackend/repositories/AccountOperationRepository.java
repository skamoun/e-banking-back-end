package iss.ma.ebankingbackend.repositories;

import iss.ma.ebankingbackend.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountOperationRepository extends JpaRepository<AccountOperation,Long> {
    Streamable<AccountOperation> findByBankAccountId(String id);

    Page<AccountOperation> findByBankAccountId(String accountId, Pageable pageable);
}
