package ru.ukhanov.t1.java.service.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ukhanov.t1.java.aspects.annotation.LogDataSourceError;
import ru.ukhanov.t1.java.aspects.annotation.Metric;
import ru.ukhanov.t1.java.dto.TransactionDto;
import ru.ukhanov.t1.java.exception.TransactionNotFoundException;
import ru.ukhanov.t1.java.mapper.TransactionMapper;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.transaction.Transaction;
import ru.ukhanov.t1.java.repository.AccountRepository;
import ru.ukhanov.t1.java.repository.TransactionRepository;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final ClientBlackList list;

    @Value("${transactions.reject.limit}")
    private int limit;


    @Override
    public TransactionDto addTransaction(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toTransaction(transactionDto);
        Account account = transaction.getAccount();

        if (account.getStatus() == null || account.getStatus() == AccountStatus.OPEN) {
            boolean isBlacklisted = list.isClientBlacklisted(account.getClient().getId().toString());
            if (isBlacklisted) {
                account.setStatus(AccountStatus.BLOCKED);
                transaction.setStatus(TransactionStatus.REJECTED);
                accountRepository.save(account);
                transactionRepository.save(transaction);
                return transactionMapper.toTransactionDto(transaction);
            }

            int rejectedCount = transactionRepository.countByAccountAndStatus(account, TransactionStatus.REJECTED);
            if (rejectedCount + 1 > limit) {
                account.setStatus(AccountStatus.ARRESTED);
            }
            transaction.setStatus(TransactionStatus.REJECTED);
            accountRepository.save(account);
            transactionRepository.save(transaction);
        }

        return transactionMapper.toTransactionDto(transaction);
    }

    @LogDataSourceError
    @Metric
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

    @Metric
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

    @LogDataSourceError
    @Metric
    @Override
    public List<TransactionDto> getALl() {
        log.debug("Receiving all transactions");
        List<TransactionDto> list = transactionRepository.findAll().stream()
                .map(transactionMapper::toTransactionDto)
                .toList();
        log.info("Retrieved {} transactions", list.size());
        return list;
    }

    @LogDataSourceError
    @Metric
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
