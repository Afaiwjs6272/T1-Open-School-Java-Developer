package ru.ukhanov.t1.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.transaction.Transaction;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findByTransactionId(UUID id);
    int countByAccountAndStatus(Account account, TransactionStatus status);
}
