package ru.ukhanov.t1.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ukhanov.t1.java.model.account.Account;

import java.util.Optional;
import java.util.UUID;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(UUID accountId);
}
