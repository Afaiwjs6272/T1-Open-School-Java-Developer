package ru.ukhanov.t1.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ukhanov.t1.java.model.account.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
