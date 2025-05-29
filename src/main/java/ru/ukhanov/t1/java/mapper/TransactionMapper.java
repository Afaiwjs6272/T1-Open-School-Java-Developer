package ru.ukhanov.t1.java.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ukhanov.t1.java.dto.TransactionDto;
import ru.ukhanov.t1.java.exception.AccountNotFoundException;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.transaction.Transaction;
import ru.ukhanov.t1.java.repository.AccountRepository;

@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final AccountRepository accountRepository;
    public TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(transaction.getId(), transaction.getAccount().getId(),
                transaction.getTransactionSum(), transaction.getTransactionTime());
    }

    public Transaction toTransaction(TransactionDto transactionDto) {
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id = " + transactionDto.getAccountId()
                ));
        return new Transaction(transactionDto.getId(), account,
                transactionDto.getTransactionSum(), transactionDto.getTransactionTime());
    }
}
