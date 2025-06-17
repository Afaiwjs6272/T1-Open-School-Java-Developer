package ru.ukhanov.t1.java.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(UUID accountId);

    List<Account> findMByStatus(AccountStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Account a SET a.status = :status WHERE a.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") AccountStatus status);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.status = 'ARRESTED'")
    int countByStatus(AccountStatus status);
}
