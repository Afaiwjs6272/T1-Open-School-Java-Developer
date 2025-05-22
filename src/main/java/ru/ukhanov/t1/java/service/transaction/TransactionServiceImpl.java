package ru.ukhanov.t1.java.service.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ukhanov.t1.java.dto.TransactionDto;
import ru.ukhanov.t1.java.exception.TransactionNotFoundException;
import ru.ukhanov.t1.java.mapper.TransactionMapper;
import ru.ukhanov.t1.java.model.transaction.Transaction;
import ru.ukhanov.t1.java.repository.TransactionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionDto addTransaction(TransactionDto transactionDto) {
        log.info("Adding new transaction: {}", transactionDto);
        Transaction transaction = transactionMapper.toTransaction(transactionDto);
        Transaction savedTransaction = transactionRepository.save(transaction);
        TransactionDto result = transactionMapper.toTransactionDto(savedTransaction);
        log.info("Transaction saved with id={}", savedTransaction.getId());
        return result;
    }

    @Override
    public TransactionDto updateTransaction(Long id, TransactionDto transactionDto) {
        log.info("Updating transaction id={}", id);
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transaction not found with id={}", id);
                    return new TransactionNotFoundException("Transaction not found with id = " + id);
                });

        Transaction updated = transactionMapper.toTransaction(transactionDto);
        updated.setId(id);
        if (updated.getAccount() == null) {
            updated.setAccount(existing.getAccount());
        }
        if (updated.getTransactionSum() == null) {
            updated.setTransactionSum(existing.getTransactionSum());
        }
        if (updated.getTransactionTime() == null) {
            updated.setTransactionTime(existing.getTransactionTime());
        }

        Transaction saved = transactionRepository.save(updated);
        TransactionDto result = transactionMapper.toTransactionDto(saved);
        log.info("Transaction id={} updated", id);
        return result;
    }

    @Override
    public TransactionDto getById(Long id) {
        log.debug("Fetching transaction by id={}", id);
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transaction not found with id={}", id);
                    return new TransactionNotFoundException("Transaction not found with id = " + id);
                });
        TransactionDto result = transactionMapper.toTransactionDto(tx);
        log.debug("Found transaction: {}", result);
        return result;
    }

    @Override
    public List<TransactionDto> getALl() {
        log.debug("Receiving all transactions");
        List<TransactionDto> list = transactionRepository.findAll().stream()
                .map(transactionMapper::toTransactionDto)
                .toList();
        log.info("Retrieved {} transactions", list.size());
        return list;
    }

    @Override
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction id={}", id);
        if (!transactionRepository.existsById(id)) {
            log.warn("Transaction with id={} not found", id);
            throw new TransactionNotFoundException("Transaction not found with id = " + id);
        }
        transactionRepository.deleteById(id);
        log.info("Transaction with id={} deleted", id);
    }
}
