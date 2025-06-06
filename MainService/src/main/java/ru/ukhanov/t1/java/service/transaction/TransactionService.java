package ru.ukhanov.t1.java.service.transaction;

import ru.ukhanov.t1.java.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    TransactionDto addTransaction(TransactionDto transactionDto);

    TransactionDto updateTransaction(Long id, TransactionDto transactionDto);

    TransactionDto getById(Long id);

    List<TransactionDto> getALl();

    void deleteTransaction(Long id);
}
